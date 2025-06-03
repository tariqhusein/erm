# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

WORKDIR /app

# 1. Copy pom.xml and pom files for dependency caching
COPY pom.xml .

# 2. Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline

# 3. Copy the rest of the source code
COPY src ./src

# 4. Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8090

# Set Spring profile to dev
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "app.jar"]