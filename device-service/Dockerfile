# =============================================================
# Stage 1: Build stage using JDK 17
# =============================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and descriptor to leverage Docker caching for dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Fix potential CRLF line endings and grant execute permissions
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Download dependencies in an isolated layer
RUN ./mvnw dependency:go-offline -B

# Copy application source code and compile
COPY src src
RUN ./mvnw clean package -DskipTests

# =============================================================
# Stage 2: Minimal runtime image
# =============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-privileged user and group for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy packaged jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose microservice HTTP port
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
