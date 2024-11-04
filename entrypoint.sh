#!/bin/sh

# Start the Spring Boot application in the background
java -jar /app/app.jar &

# Wait until the server starts
echo "Waiting for DISH to start..."
until nc -z localhost 8080; do
  sleep 5
done
echo "DISH started."

# Start cron without `-b` in the foreground
echo "Starting cron daemon..."
crond -f
