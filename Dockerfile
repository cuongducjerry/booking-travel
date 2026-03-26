# ===============================
# Build stage
# ===============================
#FROM gradle:8.5-jdk21 AS builder
#
#WORKDIR /app
#
## Copy toàn bộ project
#COPY . .
#
#RUN chmod +x gradlew
#RUN ./gradlew clean bootJar -x test --no-daemon
#
## ===============================
## Runtime stage
## ===============================
#FROM eclipse-temurin:21-jre-jammy
#
#WORKDIR /app
#
## Copy jar đã build
#COPY --from=builder /app/build/libs/*.jar app.jar
#
## Expose port
#EXPOSE 8080
#
## Chạy trực tiếp Spring Boot
#ENTRYPOINT ["java", "-jar", "app.jar"]

# ===============================
# Build stage
# ===============================
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Cài netcat để wait-for-it.sh hoạt động
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy wait-for-it + start script
COPY wait-for-it.sh /app/wait-for-it.sh
COPY start-backend.sh /app/start-backend.sh
RUN chmod +x /app/wait-for-it.sh /app/start-backend.sh

EXPOSE 8080

# Chạy script wrapper
ENTRYPOINT ["/app/start-backend.sh"]