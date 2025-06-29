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
#COPY --from=build /app/target/*.jar farm-docker.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "farm-docker.jar"]






# BUILD STAGE
#FROM maven:3.9.4-amazoncorretto-17 AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## RUNTIME STAGE
#FROM openjdk:20-slim
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]


#FROM maven:3.9.4-amazoncorretto-21-debian AS build

FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
#RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
ENV JAVA_OPTS="--enable-preview"
WORKDIR /app
COPY --from=build /app/target/farm-docker.jar farm.jar
ENTRYPOINT ["java", "-jar", "farm-docker.jar"]
