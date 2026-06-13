# syntax=docker/dockerfile:1

# ── Stage 1: Build the banking-system WAR ────────────────────────────────
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
    find /workspace/target -maxdepth 1 -type f -name "*.war" ! -name "*.original" -print -quit | xargs -I{} cp "{}" /workspace/build-output/app.war

# ── Stage 2: Minimal runtime image ─────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8080
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseSerialGC -Xss256k -XX:MaxMetaspaceSize=96m -XX:ReservedCodeCacheSize=64m -XX:TieredStopAtLevel=1 -XX:+UseCompressedOops -Djava.security.egd=file:/dev/./urandom"

COPY --from=build /workspace/build-output/app.war /app/app.war

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.war --server.port=${PORT}"]
