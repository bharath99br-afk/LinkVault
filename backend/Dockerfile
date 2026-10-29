FROM maven:3.9.16-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package \
    && cp target/backend-0.0.1-SNAPSHOT.jar target/app.jar

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S linkvault \
    && adduser -S -G linkvault linkvault

WORKDIR /app
COPY --from=build --chown=linkvault:linkvault /workspace/target/app.jar ./app.jar

USER linkvault
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
