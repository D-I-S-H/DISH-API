#!/bin/sh

# Start the Spring Boot application in the background
java -jar /app/app.jar &

# Wait until the server starts
echo "Waiting for the Spring Boot application to start..."
until nc -z localhost 8080; do
  sleep 5
done
echo "Application started."

# Start cron in the background
echo "Starting cron daemon..."
crond -b

# Keep the container running
wait -n
