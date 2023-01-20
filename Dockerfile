FROM openjdk:17-alpine

WORKDIR /app

COPY ./target/TestingMicroservices-0.0.1-SNAPSHOT.jar .

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "TestingMicroservices-0.0.1-SNAPSHOT.jar"]

