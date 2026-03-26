FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package
#new image for runtime phase
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 4006
#run when container start
ENTRYPOINT ["java", "-jar", "app.jar"]