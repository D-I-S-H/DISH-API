PRAGMA foreign_keys = ON;

CREATE TABLE location (
name TEXT PRIMARY KEY,
apiuuid CHAR(24)
);

CREATE TABLE accounts (
username TEXT NOT NULL,
password BLOB NOT NULL,
uid INTEGER PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE time(
mealTime TEXT PRIMARY KEY
);

CREATE TABLE menuItems (
name TEXT,
ingredients TEXT,
portion TEXT,
description TEXT,
nutrients TEXT,
calories INTEGER,
time TEXT,
location TEXT,
Allergens TEXT,
PRIMARY KEY (name, location),
FOREIGN KEY (time) REFERENCES time(mealTime),
FOREIGN KEY (location) REFERENCES location(name)
);
