# syntax=docker/dockerfile:1

# ── Stage 1: Build only the banking-system JAR ──────────────────────────
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only POM first to cache dependencies
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline -B || true

# Copy source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests -B && \
    mkdir -p /workspace/build-output && \
    find /workspace/target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" -print -quit | xargs -I{} cp "{}" /workspace/build-output/app.jar

# ── Stage 2: Minimal runtime image ─────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC -Xss512k -XX:MaxMetaspaceSize=128m -Djava.security.egd=file:/dev/./urandom"

COPY --from=build /workspace/build-output/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar --server.port=${PORT}"]
