FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./build/libs/woojoobook-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
