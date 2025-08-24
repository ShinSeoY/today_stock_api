FROM openjdk:17-jdk

ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} backend.jar

ARG PROFILE

ENV PROFILE_ENV=${PROFILE}

ENTRYPOINT ["java", "-Xms2048M", "-Xmx2048M", "-jar", "-Dspring.profiles.active=${PROFILE_ENV}", "backend.jar"]
