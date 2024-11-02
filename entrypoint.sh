#!/bin/sh

# Run at startup
python3 /app/chartwells_query.py

# Start dcron
crond -b

touch ./debug.file

# Start DISH
java -jar /app/app.jar
