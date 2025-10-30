# https://medium.com/@franna.jaya/containerized-your-spring-3-3-with-java-21-by-fran-na-jaya-9b623fdae2a0
# Helped with ChatGPT

# ===============================
# Build stage (use Maven)
# ===============================
FROM maven:3.9.9-amazoncorretto-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven config first (for caching dependencies)
COPY pom.xml .
# RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the Spring Boot application
RUN mvn clean package -DskipTests

# ===============================
# Runtime stage (use JDK only)
# ===============================
FROM amazoncorretto:21.0.4-alpine3.18

# Set working directory in the runtime image
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for the REST API
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
