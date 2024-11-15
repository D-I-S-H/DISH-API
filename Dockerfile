FROM gradle:7.6.0-jdk17 AS build

WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY src /app/src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine

# Install Python, dcron, and SSH
RUN apk update && \
    apk add python3 py3-pip dcron py3-requests openssh && \
    echo "root:Docker!" | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#Port 22/Port 2222/' /etc/ssh/sshd_config && \
    ssh-keygen -A

WORKDIR /app

# Copy JAR and Python script
COPY --from=build /app/build/libs/*.jar app.jar
COPY chartwells_query.py /app/chartwells_query.py

# Add crontab file and set permissions
COPY crontab.txt /etc/crontabs/root

# Set environment variables
ENV RUNNING_IN_DOCKER=true
ENV DATABASE_URL=jdbc:sqlite:/app/Database/dish.db

# Make entrypoint executable
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080 2222

# Create Database directory with correct permissions
RUN mkdir -p /app/Database && chmod -R 777 /app/Database

# Start SSH and the application
CMD /usr/sbin/sshd -D & /entrypoint.sh
