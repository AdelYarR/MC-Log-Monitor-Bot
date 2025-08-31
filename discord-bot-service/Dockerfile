FROM maven:3.8.7-openjdk-18-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY .env .

RUN mvn clean package

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder app/target/discord-bot-service.jar .
COPY --from=builder app/.env .

CMD ["java", "-jar", "discord-bot-service.jar"]
