FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Find the JAR file and copy it
RUN cp target/*.jar app.jar

# Run the application
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]