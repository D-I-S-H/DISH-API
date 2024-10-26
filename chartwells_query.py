import sqlite3
import requests
import json
from datetime import datetime, timezone, timedelta
import time

#used to access the chartwells api from the viewpoint of a user normal
headers = {
    'User-Agent': 'Mozilla/5.0'
}

date_today = datetime.now(timezone(timedelta(hours=-4))).strftime('%Y-%m-%d');
date_tomorrow = (datetime.now(timezone(timedelta(hours=-4))) + timedelta(1)).strftime('%Y-%m-%d')
dates = {"today":date_today}#,"tomorrow":date_tomorrow}

#used to grab period data for a day and location
period_request = "https://api.dineoncampus.com/v1/location/{location}/periods?platform=0&date={date}"

#used to grab meal data for a given location, period, and date
meal_data_request = "https://api.dineoncampus.com/v1/location/{location}/periods/{period}?platform=0&date={date}"

dining_locations = {"Wadsworth":"64b9990ec625af0685fb939d"}#,"McNair":"64a6b628351d5305dde2bc08","DHH":"64e3da15e45d430b80c9b981"}

def main():
    db_conection = sqlite3.connect("database.db")
    db_cursor = db_conection.cursor()

    for date in dates:
        for location in dining_locations:
            print(f"Grabbing periods for {date} {location}")
            periods = get_periods(date, location)

            print(periods)
            #TODO: update periods with current values, will be tricky
            #db_cursor.executemany("INSERT INTO time VALUES (:name, :)")

            for period in periods:
                meal_json = get_meal_data(periods[period], date, location)
                if meal_json != -1:
                    for station in meal_json["menu"]["periods"]["categories"]:
                        print(station)    
                        station["location"] = location
                    db_cursor.executemany("INSERT OR REPLACE INTO stations VALUES (:name, :location)", meal_json["menu"]["periods"]["categories"])

                    for station in meal_json["menu"]["periods"]["categories"]:
                        for meal in station["items"]:
                            meal["time"] = period
                            meal["location"] = location
                            meal["station"] = station["name"]
                            meal["nutrients_json"] = str(meal["nutrients"])
                            meal["filters_json"] = str(meal["filters"])
                        db_cursor.executemany("INSERT INTO menuItems VALUES (:name, :station, :ingredients, :portion, :desc, :nutrients_json, :calories, :time, :location, :filters_json)", station["items"])


                    db_conection.commit()


def get_periods(date, location):
    request_string = period_request.format(date = dates[date], location = dining_locations[location])
    print(request_string)
    response = requests.get(request_string, headers=headers, timeout=10)
    time.sleep(6)
    response.raise_for_status()
    if response.status_code != 204:
        response_json = response.json()

        periods = {}
        if not response_json["closed"]:
            for period in response_json["periods"]:
                periods[period["name"]] = period["id"]
        return periods
    else:
        #TODO: Throw a nasty error here or something
        print(f"ERROR: status code 204 not returned for {date} {location}")
        return {}

def get_meal_data(period, date, location):
    request_string =  meal_data_request.format(period = period, date = dates[date], location = dining_locations[location])
    print(request_string)
    response = requests.get(request_string, headers=headers, timeout=10)
    time.sleep(6)
    response.raise_for_status()
    if response.status_code != 204:
        response_json = response.json()

        return response_json;
    else:
        #TODO: Throw a nasty error here or something
        print(f"ERROR: status code 204 not returned for {period} {date} {location}")
        return -1



if __name__=="__main__":
    main()
