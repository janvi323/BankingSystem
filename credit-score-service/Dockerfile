# syntax=docker/dockerfile:1

FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests && \
    mkdir -p /workspace/build-output && \
    find /workspace/target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" -print -quit | xargs -I{} cp "{}" /workspace/build-output/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

COPY --from=build /workspace/build-output/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar --server.port=${PORT}"]
