FROM openjdk:8-jre-alpine
COPY target/publisher-java-rabbit-1.0-SNAPSHOT.jar /usr/local/lib/amqp-publisher.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/amqp-publisher.jar"]
