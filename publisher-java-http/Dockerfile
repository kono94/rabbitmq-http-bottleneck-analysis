FROM java:8-jdk-alpine
COPY ./out/artifacts/publisher_java_http/publisher-java-http.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "publisher-java-http.jar"]
#  "docker.for.mac.localhost:2022/endpoint", "docker.for.mac.localhost:2023/endpoint"
