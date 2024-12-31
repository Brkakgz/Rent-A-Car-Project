# Java 21 için uygun bir OpenJDK imajı
FROM openjdk:21-jdk-slim

# Add a volume pointing to /tmp
VOLUME /tmp

# Copy application jar to the container
ARG JAR_FILE=target/rentacar6-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
