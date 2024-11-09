import sqlite3
import requests
import json
from datetime import datetime, timezone, timedelta
import time
import os
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.util.retry import Retry

# Used to access the Chartwells API from the viewpoint of a normal user
headers = {
    'User-Agent': 'Mozilla/5.0'
}

# Conditional to determine if the script is running in a Docker container
if os.getenv("RUNNING_IN_DOCKER") == "true":
    database_path = "/app/Database/dish.db"
else:
    database_path = "Database/dish.db"

date_today = datetime.now(timezone(timedelta(hours=-4))).strftime('%Y-%m-%d')
date_tomorrow = (datetime.now(timezone(timedelta(hours=-4))) + timedelta(1)).strftime('%Y-%m-%d')

dates = {"today":date_today, "tomorrow":date_tomorrow}

# URLs for requests
period_request = "https://api.dineoncampus.com/v1/location/{location}/periods?platform=0&date={date}"
meal_data_request = "https://api.dineoncampus.com/v1/location/{location}/periods/{period}?platform=0&date={date}"

dining_locations = {}

# Initialize a session with retries
session = requests.Session()
retries = Retry(total=5, backoff_factor=1, status_forcelist=[429, 500, 502, 503, 504])
session.mount('https://', HTTPAdapter(max_retries=retries))

def main():
    db_conection = sqlite3.connect(database_path)
    db_cursor = db_conection.cursor()

    # Get locations from database
    db_cursor.execute("SELECT * FROM locations")
    rows = db_cursor.fetchall()
    for row in rows:
        dining_locations[row[0]] = row[1]

    for date in dates:
        for location in dining_locations:
            print(f"Grabbing periods for {dates[date]} {location}")  # Using the value of dates[date]
            periods = get_periods(date, location)

            db_cursor.executemany("INSERT OR REPLACE INTO time VALUES (:name, :UUID)", periods)

            for period in periods:
                meal_json = get_meal_data(period["UUID"], date, location)
                if meal_json != -1:
                    for station in meal_json["menu"]["periods"]["categories"]:
                        station["location"] = location
                    db_cursor.executemany("INSERT OR REPLACE INTO stations VALUES (:name, :location)", meal_json["menu"]["periods"]["categories"])

                    for station in meal_json["menu"]["periods"]["categories"]:
                        for item in station["items"]:
                            item["date"] = date
                            item["time"] = period["name"]
                            item["date"] = dates[date]
                            item["location"] = location
                            item["station"] = station["name"]
                            item["nutrients_json"] = handle_nutrients(db_cursor, item)

                            allergens_list = []
                            filters_list = []
                            for filter in item["filters"]:
                                if filter["type"] == "allergen":
                                    allergens_list.append(filter["name"])
                                else:
                                    filters_list.append(filter["name"])
                            item["allergens_json"] = str(allergens_list)
                            item["filters_json"] = str(filters_list)

                        db_cursor.executemany("INSERT OR REPLACE INTO menuItems VALUES (:name, :station, :ingredients, :portion, :desc, :nutrients_json, :calories, :date, :time, :location, :allergens_json, :filters_json)", station["items"])

                        for item in station["items"]:
                            db_cursor.executemany("INSERT OR REPLACE INTO menuFilters VALUES (:name, :type)", item["filters"])
                            for filter in item["filters"]:
                                db_cursor.execute("INSERT OR REPLACE INTO itemFilterAssociations VALUES (?, ?, ?, ?, ?, ?)", [item["name"], item["location"], item["date"], item["time"], item["station"], filter["name"]])
            db_conection.commit()


def get_periods(date, location):
    request_string = period_request.format(date=dates[date], location=dining_locations[location])
    try:
        response = session.get(request_string, headers=headers, timeout=30)
        response.raise_for_status()
        time.sleep(6)  # Delay to avoid rate-limiting
        if response.status_code != 204:
            response_json = response.json()
            period_list = []
            if not response_json["closed"]:
                for period in response_json["periods"]:
                    periods = {
                        "name": period["name"],
                        "UUID": period["id"]
                    }
                    period_list.append(periods)
            return period_list
        else:
            print(f"ERROR: Status code 204 not returned for {date} {location}")
            return []
    except requests.exceptions.Timeout:
        print(f"Timeout error when accessing {request_string}")
        return []
    except requests.exceptions.RequestException as e:
        print(f"Request error: {e}")
        return []

def get_meal_data(period, date, location):
    request_string = meal_data_request.format(period=period, date=dates[date], location=dining_locations[location])
    try:
        response = session.get(request_string, headers=headers, timeout=30)
        response.raise_for_status()
        time.sleep(6)  # Delay to avoid rate-limiting
        if response.status_code != 204:
            response_json = response.json()
            return response_json
        else:
            print(f"ERROR: Status code 204 not returned for {period} {date} {location}")
            return -1
    except requests.exceptions.Timeout:
        print(f"Timeout error when accessing {request_string}")
        return -1
    except requests.exceptions.RequestException as e:
        print(f"Request error: {e}")
        return -1

def handle_nutrients(db_cursor, item):
    nutrients_list = []
    nutrients_association_list = []
    for nutrient in item["nutrients"]:
        dictionary = {
            "name": nutrient["name"],
            "value": nutrient["value"],
            "uom": nutrient["uom"]
        }
        nutrients_list.append(dictionary.copy())
        dictionary.update({
            "itemName": item["name"],
            "itemLocation": item["location"],
            "itemDate": item["date"],
            "itemTime": item["time"],
            "itemStation": item["station"]
        })
        nutrients_association_list.append(dictionary.copy())
    db_cursor.executemany("INSERT OR REPLACE INTO menuNutrients VALUES (:name, :value, :uom, :value_numeric)", item["nutrients"])
    db_cursor.executemany("INSERT OR REPLACE INTO itemNutrientAssociations VALUES (:itemName, :itemLocation, :itemDate, :itemTime, :itemStation, :name, :value)", nutrients_association_list)
    return str(nutrients_list)

if __name__ == "__main__":
    main()
