PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS locations (
    name TEXT PRIMARY KEY,
    apiUUID CHAR(24)
);

CREATE TABLE IF NOT EXISTS accounts (
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    uid INTEGER PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE IF NOT EXISTS time (
    mealTime TEXT PRIMARY KEY,
    apiUUID CHAR(24)
);

CREATE TABLE IF NOT EXISTS stations (
    stationName TEXT,
    locationName TEXT,
    PRIMARY KEY (stationName, locationName),
    FOREIGN KEY (locationName) REFERENCES locations(name)
);

CREATE TABLE IF NOT EXISTS menuItems (
    name TEXT,
    station TEXT,
    ingredients TEXT,
    portion TEXT,
    description TEXT,
    nutrients TEXT,
    calories INTEGER,
    time TEXT,
    location TEXT,
    allergens TEXT,
    labels TEXT,
    PRIMARY KEY (name, location, time, station),
    FOREIGN KEY (time) REFERENCES time(mealTime),
    FOREIGN KEY (location) REFERENCES location(name),
    FOREIGN KEY (station) references stations(stationName)
);

CREATE TABLE IF NOT EXISTS menuFilters (
    name TEXT primary key,
    type TEXT
);

CREATE TABLE IF NOT EXISTS itemFilterAssociations (
    itemName TEXT,
    itemLocation TEXT,
    itemTime TEXT,
    itemStation TEXT,
    filterName TEXT,
    PRIMARY KEY (itemName, itemLocation, itemTime, itemStation, filterName),
    FOREIGN KEY (itemName) REFERENCES menuItems(name),
    FOREIGN KEY (itemLocation) REFERENCES menuItems(location),
    FOREIGN KEY (itemTime) REFERENCES menuItems(time),
    FOREIGN KEY (itemStation) REFERENCES menuItems(station),
    FOREIGN KEY (filterName) REFERENCES menuItems(name)
);

CREATE TABLE IF NOT EXISTS menuNutrients (
    name TEXT,
    value TEXT,
    uom TEXT,
    valueNumeric TEXT,
    PRIMARY KEY (name, value)
);

CREATE TABLE IF NOT EXISTS itemNutrientAssociations (
    itemName TEXT,
    itemLocation TEXT,
    itemTime TEXT,
    itemStation TEXT,
    nutrientName TEXT,
    nutrientValue TEXT,
    PRIMARY KEY (itemName, itemLocation, itemTime, itemStation, nutrientName, nutrientValue),
    FOREIGN KEY (itemName) REFERENCES menuItems(name),
    FOREIGN KEY (itemLocation) REFERENCES menuItems(location),
    FOREIGN KEY (itemTime) REFERENCES menuItems(time),
    FOREIGN KEY (itemStation) REFERENCES menuItems(station),
    FOREIGN KEY (nutrientName) REFERENCES menuNutrients(name),
    FOREIGN KEY (nutrientValue) REFERENCES menuNutrients(value)
);

INSERT INTO locations(name, apiUUID) VALUES
('Wadsworth', '64b9990ec625af0685fb939d'),
('McNair', '64a6b628351d5305dde2bc08'),
('DHH', '64e3da15e45d430b80c9b981');
