# =========================
# Build stage
# =========================
FROM gradle:8.7-jdk17 AS build

WORKDIR /app

# Copy gradle config trước để cache dependency
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Copy source
COPY src src

# Build jar
RUN ./gradlew bootJar --no-daemon

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy jar từ build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
