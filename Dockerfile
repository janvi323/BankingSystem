# syntax=docker/dockerfile:1

FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY . .

RUN chmod +x ./mvnw && \
    ./mvnw clean package -DskipTests && \
    cd credit-score-service && \
    ../mvnw clean package -DskipTests

RUN mkdir -p /workspace/build-output/banking /workspace/build-output/credit-score && \
    find /workspace/target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" -print -quit | xargs -I{} cp "{}" /workspace/build-output/banking/app.jar && \
    find /workspace/credit-score-service/target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" -print -quit | xargs -I{} cp "{}" /workspace/build-output/credit-score/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8080
ENV CREDIT_SCORE_PORT=8083
ENV CREDIT_SCORE_SERVICE_URL=http://127.0.0.1:8083
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

COPY --from=build /workspace/build-output/banking/app.jar /app/banking/
COPY --from=build /workspace/build-output/credit-score/app.jar /app/credit-score/
COPY docker/start.sh /app/start.sh

RUN chmod +x /app/start.sh

EXPOSE 8080

ENTRYPOINT ["/app/start.sh"]
