# Phase 1: Build the Java application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Phase 2: Run the application using a lightweight JRE image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from:build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
