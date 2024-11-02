FROM gradle:7.6.0-jdk17 AS build

# Set the working directory 
WORKDIR /app

# Copy the Gradle configuration and source code
COPY build.gradle settings.gradle /app/
COPY src /app/src

# Build the application
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Rub
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
