FROM java:8
COPY build/libs/pubsub-0.0.1-SNAPSHOT.jar /service.jar
COPY src/main/resources/key.json /key.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/key.json
CMD ["java", "-jar", "/service.jar"]

