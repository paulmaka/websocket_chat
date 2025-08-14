FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY ./common ./common
COPY ./server ./server
COPY ./pom.xml ./pom.xml

RUN mvn -f ./common clean install -DskipTests
RUN mvn -f ./server clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/server/target/server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
