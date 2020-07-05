#
# Build stage
#
FROM maven:3.5-jdk-8 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn dependency:go-offline
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:8-jre-alpine
COPY --from=build /home/app/target/publisher-java-rabbit-1.0-SNAPSHOT.jar /usr/local/lib/amqp-publisher.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/amqp-publisher.jar"]
