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
    Allergens TEXT,
    PRIMARY KEY (name, location, time, station),
    FOREIGN KEY (time) REFERENCES time(mealTime),
    FOREIGN KEY (location) REFERENCES location(name),
    FOREIGN KEY (station) references stations(stationName)
);

INSERT INTO locations(name, apiUUID) VALUES
('Wadsworth', '64b9990ec625af0685fb939d'),
('McNair', '64a6b628351d5305dde2bc08'),
('DHH', '64e3da15e45d430b80c9b981');

INSERT INTO time VALUES
('Breakfast', '66c25f78351d5300dd7d1807'),
('Lunch', '66c25f78351d5300dd7d17fd'),
('Dinner', '66c25f78351d5300dd7d1804'),
('Everyday', '66cf452dc625af06298b134c');
