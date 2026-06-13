#!/usr/bin/env sh
set -eu

: "${PORT:=8080}"
: "${DB_URL:?DB_URL environment variable is required}"
: "${DB_USER:?DB_USER environment variable is required}"
: "${DB_PASSWORD:?DB_PASSWORD environment variable is required}"

echo "Starting credit-score-service on port ${PORT}"
exec java ${JAVA_OPTS:-} -jar /app/app.jar \
  --server.port="${PORT}" \
  --spring.datasource.url="${DB_URL}" \
  --spring.datasource.username="${DB_USER}" \
  --spring.datasource.password="${DB_PASSWORD}"
