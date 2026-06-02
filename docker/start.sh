#!/usr/bin/env sh
set -eu

: "${PORT:=8080}"
: "${CREDIT_SCORE_PORT:=8083}"
: "${CREDIT_SCORE_SERVICE_URL:=http://127.0.0.1:${CREDIT_SCORE_PORT}}"

CREDIT_SCORE_JAR="$(find /app/credit-score -maxdepth 1 -type f -name '*.jar' | head -n 1)"
BANKING_JAR="$(find /app/banking -maxdepth 1 -type f -name '*.jar' | head -n 1)"

if [ -z "${CREDIT_SCORE_JAR}" ]; then
  echo "Credit score service JAR was not found." >&2
  exit 1
fi

if [ -z "${BANKING_JAR}" ]; then
  echo "Banking application JAR was not found." >&2
  exit 1
fi

echo "Starting credit-score-service on port ${CREDIT_SCORE_PORT}"
java ${JAVA_OPTS:-} -jar "${CREDIT_SCORE_JAR}" \
  --server.port="${CREDIT_SCORE_PORT}" &

CREDIT_SCORE_PID="$!"

cleanup() {
  kill "${CREDIT_SCORE_PID}" 2>/dev/null || true
}
trap cleanup INT TERM EXIT

echo "Starting banking-system on port ${PORT}"
exec java ${JAVA_OPTS:-} -jar "${BANKING_JAR}" \
  --server.port="${PORT}" \
  --credit.score.service.url="${CREDIT_SCORE_SERVICE_URL}"
