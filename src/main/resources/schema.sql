PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS locations (
    name TEXT PRIMARY KEY,
    apiuuid CHAR(24)
);

CREATE TABLE IF NOT EXISTS accounts (
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    uid INTEGER PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE IF NOT EXISTS time (
    mealTime TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS menuItems (
    name TEXT,
    ingredients TEXT, -- JSON Object
    portion TEXT,
    description TEXT,
    nutrients TEXT, -- JSON Object
    calories INTEGER,
    time TEXT,
    location TEXT,
    Allergens TEXT, -- Array
    PRIMARY KEY (name, location),
    FOREIGN KEY (time) REFERENCES time(mealTime),
    FOREIGN KEY (location) REFERENCES locations(name)
);