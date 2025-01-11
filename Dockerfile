# Use the base image OpenJDK 8
FROM openjdk:8-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY target/main-0.0.1-SNAPSHOT.jar app.jar

# Specify the command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
