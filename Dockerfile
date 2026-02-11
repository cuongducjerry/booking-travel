# ===============================
# Build stage
# ===============================
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy toàn bộ project
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

# ===============================
# Runtime stage
# ===============================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]