FROM maven:3.8.7-openjdk-18-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY .env .

RUN mvn clean package

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/log-tailer-service.jar .
COPY --from=builder /app/.env .
COPY --from=builder /app/src/main/resources/ ./src/main/resources

CMD ["java", "-jar", "log-tailer-service.jar"]