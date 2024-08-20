FROM amazoncorretto:21-alpine3.18-jdk

COPY rest/target/rest-0.0.1-SNAPSHOT.jar hotel-app.jar

ENTRYPOINT ["java","-jar","/hotel-app.jar"]