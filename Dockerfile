# ================================
# 1. Build Stage (Maven + JDK)
# ================================
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copy Maven descriptor first (to cache dependencies)
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn -q -e -DskipTests package

# ================================
# 2. Runtime Stage (Slim JRE)
# ================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Railway exposes PORT as environment variable
ENV PORT=8080
EXPOSE 8080

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
