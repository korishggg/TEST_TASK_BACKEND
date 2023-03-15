FROM openjdk:17

# Set the working directory to /app
WORKDIR /app

# Copy the user-service-app jar file into the container at /app
COPY target/user-service-app.jar /app

# Expose port 8080 to the host machine
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["java", "-jar", "user-service-app.jar"]