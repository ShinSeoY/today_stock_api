FROM gradle:8.7-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew dependencies || true

COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ARG PROFILE=prod
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
