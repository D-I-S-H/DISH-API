PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS locations (
    name TEXT PRIMARY KEY,
    apiuuid CHAR(24)
);

CREATE TABLE IF NOT EXISTS accounts (
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    uid INTEGER PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE IF NOT EXISTS time (
    mealTime TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS menuItems (
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

CREATE TABLE IF NOT EXISTS ratings (
menuItemName TEXT,
menuItemLocation TEXT,
accountUsername TEXT,
rating INTEGER,
PRIMARY KEY (menuItemName, accountUsername),
FOREIGN KEY (menuItemName) REFERENCES menuItems(name),
FOREIGN KEY (menuItemLocation) REFERENCES menuItems(location),
FOREIGN KEY (accountUsername) REFERENCES accounts(username)
);
