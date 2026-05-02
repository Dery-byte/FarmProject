#
#
#FROM maven:3.9.4-eclipse-temurin-21 AS build
#WORKDIR /app
#COPY pom.xml .
##RUN mvn dependency:go-offline
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM openjdk:21-slim
#ENV JAVA_OPTS="--enable-preview"
#WORKDIR /app
#COPY --from=build /app/target/farm-docker.jar farm-docker.jar
#ENTRYPOINT ["java",  "--enable-preview", "-jar", "farm-docker.jar"]



# -------- BUILD STAGE --------
# -------- BUILD STAGE --------
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY --from=build /app/target/farm-docker.jar app.jar

ENTRYPOINT ["java", "-Xms64m", "-Xmx180m", "--enable-preview", "-jar", "app.jar"]
