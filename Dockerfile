# --- build stage (optional) ---
# FROM eclipse-temurin:17-jdk-jammy as build

# --- runtime stage ---
FROM eclipse-temurin:17.0.11_9-jre-jammy

ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} backend.jar

ARG PROFILE

ENV PROFILE_ENV=${PROFILE}

ENTRYPOINT ["java", "-Xms2048M", "-Xmx2048M", "-Dspring.profiles.active=${PROFILE_ENV}", "-jar", "backend.jar"]
