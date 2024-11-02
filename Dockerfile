FROM gradle:7.6.0-jdk17 AS build

WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY src /app/src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine

# Install Python and dcron
RUN apk update && apk add python3 py3-pip dcron py3-requests

WORKDIR /app

# Copy JAR and Python script
COPY --from=build /app/build/libs/*.jar app.jar
COPY chartwells_query.py /app/chartwells_query.py

# Add crontab file and set permissions
COPY crontab.txt /etc/crontabs/root

# Make entrypoint executable
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
