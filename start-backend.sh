#!/bin/sh

echo "Waiting for Redis..."
/app/wait-for-it.sh redis:6379 --timeout=60 --strict

echo "Waiting for Postgres..."
/app/wait-for-it.sh db-postgres:5432 --timeout=60 --strict

echo "Starting Spring Boot..."
exec java -jar /app/app.jar