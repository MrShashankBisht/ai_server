# ==========================================
# Stage 1: Build Stage (The Factory)
# ==========================================
FROM gradle:8.8-jdk21-alpine AS builder

WORKDIR /home/gradle/project

# 1. Cache Gradle dependencies first (avoids re-downloading on code changes)
COPY --chown=gradle:gradle gradlew settings.gradle.kts build.gradle.kts gradle.properties* ./
COPY --chown=gradle:gradle gradle ./gradle
RUN ./gradlew dependencies --no-daemon --stacktrace || true

# 2. Copy source code and build the Fat JAR using the updated Goooler Shadow plugin
COPY --chown=gradle:gradle src ./src
RUN ./gradlew shadowJar --no-daemon -x test

# ==========================================
# Stage 2: Enterprise Production Runtime
# ==========================================
# Google Distroless: No shell, no OS packages, pure Java environment
FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app

# Copy only the compiled Fat JAR from the builder stage
# The Shadow plugin automatically appends "-all.jar" to the output file
COPY --from=builder --chown=nonroot:nonroot /home/gradle/project/build/libs/*-all.jar /app/app.jar

# Run as the built-in restricted user provided by Distroless
USER nonroot

EXPOSE 8090

# JVM memory constraints tuned specifically for an 8 GB RAM server
ENV JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -XX:MaxRAMPercentage=50.0 -XX:MinRAMPercentage=20.0 -Xss512k -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]