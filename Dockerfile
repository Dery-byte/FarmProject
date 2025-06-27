#
#
## BUILD STAGE
#FROM maven:3.9.4-amazoncorretto-20-debian AS build
#WORKDIR /app
##COPY pom.xml .
#COPY . .
#RUN mvn clean package -DskipTests
#
## RUNTIME STAGE
#FROM openjdk:22-jdk-slim
#WORKDIR /app
#COPY --from=build /app/target/exam-docker.jar exam-docker.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "exam-docker.jar"]







# BUILD STAGE - Using different Maven version
FROM maven:3.8.6-amazoncorretto-11 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# RUNTIME STAGE
FROM amazoncorretto:11-alpine-jdk
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/target/*.jar exam-docker.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "exam-docker.jar"]