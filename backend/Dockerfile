# Build stage
FROM maven:3.8.4-openjdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/smart-campus.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/smart-campus.jar"]
