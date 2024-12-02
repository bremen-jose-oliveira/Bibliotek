
#use and Official Maven Imaga to build the Spring  Boot App
FROM maven:3.9.8-amazoncorretto-21

#Set the working Directory

WORKDIR /app

#copy the pom and install dependency

COPY pom.xml .
RUN mvn dependency:go-OFFLINE


#Copy the Source code and  build the aplication
COPY src ./src
RUN mvn clean  package -DskipTests

#Use and Official Open JDK  image to run  the aplication

FROM openjdk:21

#Set the working Directory
WORKDIR /app

#Copy the Build jar file  from the build stage

COPY --from=build /app/target/bibliotek-0.0.1-SNAPSHOT.jar .

#Expose the Port

EXPOSE 8080

#Epeciafy  the command  to run the aplication

ENTRYPOINT ["java", "-jar",".jar", "app/bibliotek-0.0.1-SNAPSHOT.jar"]
