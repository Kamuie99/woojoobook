FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./build/libs/woojoobook-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
