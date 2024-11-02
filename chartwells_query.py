import sqlite3
import requests
import json
from datetime import datetime, timezone, timedelta
import time

#used to access the chartwells api from the viewpoint of a user normal
headers = {
    'User-Agent': 'Mozilla/5.0'
}

#database_path = "database.db"
database_path = "Database/dish.db"

date_today = datetime.now(timezone(timedelta(hours=-4))).strftime('%Y-%m-%d')
date_tomorrow = (datetime.now(timezone(timedelta(hours=-4))) + timedelta(1)).strftime('%Y-%m-%d')
dates = {"today":date_today,"tomorrow":date_tomorrow}

#used to grab period data for a day and location
period_request = "https://api.dineoncampus.com/v1/location/{location}/periods?platform=0&date={date}"

#used to grab meal data for a given location, period, and date
meal_data_request = "https://api.dineoncampus.com/v1/location/{location}/periods/{period}?platform=0&date={date}"

dining_locations = {"Wadsworth":"64b9990ec625af0685fb939d","McNair":"64a6b628351d5305dde2bc08","DHH":"64e3da15e45d430b80c9b981"}

def main():
    db_conection = sqlite3.connect(database_path)
    db_cursor = db_conection.cursor()

    for date in dates:
        for location in dining_locations:
            print(f"Grabbing periods for {date} {location}")
            periods = get_periods(date, location)

            print(periods)

            #db_cursor.executemany("DELETE FROM time WHERE mealTime=:name", periods)
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
    request_string = period_request.format(date = dates[date], location = dining_locations[location])
    print(request_string)
    response = requests.get(request_string, headers=headers, timeout=10)
    time.sleep(6)
    response.raise_for_status()
    if response.status_code != 204:
        response_json = response.json()

        period_list = []
        if not response_json["closed"]:
            for period in response_json["periods"]:
                periods = {}
                periods["name"] = period["name"]
                periods["UUID"] = period["id"]
                period_list.append(periods)
        return period_list
    else:
        #TODO: Throw a nasty error here or something
        print(f"ERROR: status code 204 not returned for {date} {location}")
        return []

def get_meal_data(period, date, location):
    request_string =  meal_data_request.format(period = period, date = dates[date], location = dining_locations[location])
    print(request_string)
    response = requests.get(request_string, headers=headers, timeout=10)
    time.sleep(6)
    response.raise_for_status()
    if response.status_code != 204:
        response_json = response.json()

        return response_json
    else:
        #TODO: Throw a nasty error here or something
        print(f"ERROR: status code 204 not returned for {period} {date} {location}")
        return -1

def handle_nutrients(db_cursor, item):
    nutrients_list = []
    nutrients_association_list = []
    for nutrient in item["nutrients"]:
        dictionary = {}
        dictionary["name"] = nutrient["name"]
        dictionary["value"] = nutrient["value"]
        dictionary["uom"] = nutrient["uom"]
        nutrients_list.append(dictionary.copy())
        dictionary["itemName"] = item["name"]
        dictionary["itemLocation"] = item["location"]
        dictionary["itemDate"] = item["date"]
        dictionary["itemTime"] = item["time"]
        dictionary["itemStation"] = item["station"]
        nutrients_association_list.append(dictionary.copy())
    db_cursor.executemany("INSERT OR REPLACE INTO menuNutrients VALUES (:name, :value, :uom, :value_numeric)", item["nutrients"])
    db_cursor.executemany("INSERT OR REPLACE INTO itemNutrientAssociations VALUES (:itemName, :itemLocation, :itemDate, :itemTime, :itemStation, :name, :value)", nutrients_association_list)
    return str(nutrients_list)

if __name__=="__main__":
    main()
