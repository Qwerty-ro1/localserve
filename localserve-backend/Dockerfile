# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN tr -d '\r' < mvnw > mvnw_unix && chmod +x mvnw_unix
RUN ./mvnw_unix dependency:go-offline -q

COPY src ./src
RUN ./mvnw_unix clean package -DskipTests -q

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]