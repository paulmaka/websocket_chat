FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY ../common/pom.xml ./common/pom.xml
COPY ../common/src ./common/src
COPY ./pom.xml .
COPY ./src ./src

RUN mvn -f ./common clean package -DskipTests
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/websocket_chat_server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]