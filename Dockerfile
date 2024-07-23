FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./build/libs/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
