#!/bin/sh


java -jar /app/app.jar &

# Wait until server starts
echo "Waiting for the Spring Boot application to start..."
until nc -z localhost 8080; do
  sleep 5
done
echo "Started"

# Run python script
python3 /app/chartwells_query.py

# Start cron in the background
crond -b

wait -n
