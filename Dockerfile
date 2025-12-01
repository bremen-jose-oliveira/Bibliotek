# Use an Official Maven Image to build the Spring Boot App
FROM maven:3.9.8-amazoncorretto-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use Eclipse Temurin (official OpenJDK) image to run the application
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/bibliotek-0.0.1-SNAPSHOT.jar .

# Expose the port
EXPOSE 8080

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "bibliotek-0.0.1-SNAPSHOT.jar"]
