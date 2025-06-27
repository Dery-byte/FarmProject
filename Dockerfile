

# BUILD STAGE
FROM maven:3.9.4-amazoncorretto-20-debian AS build
WORKDIR /app
#COPY pom.xml .
COPY . .
RUN mvn clean package -DskipTests

# RUNTIME STAGE
FROM openjdk:20-jdk-slim
WORKDIR /app
COPY --from=build /app/target/exam-docker.jar exam-docker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "exam-docker.jar"]