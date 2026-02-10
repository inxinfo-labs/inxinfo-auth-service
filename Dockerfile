# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and project
COPY . .

# Build app-runner (skip tests for faster image build); use wrapper so no system Maven needed
RUN chmod +x mvnw 2>/dev/null || true && ./mvnw -B clean package -DskipTests -pl app-runner -am

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 app && adduser -u 1000 -G app -D app
USER app

# Copy jar from builder (path from root: app-runner/target/app-runner-*.jar)
COPY --from=builder /app/app-runner/target/app-runner-*.jar app.jar

# PORT is set by Render
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
