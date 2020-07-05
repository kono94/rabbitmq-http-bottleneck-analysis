Start rabbitMQ:
```
docker-compose up -d
```


Build the java projects manually:
```cd publisher-java-http
mvn clean package
cd ../publisher-java-rabbit
mvn clean package
cd ..
```

HTTP sequence:
```
./startContainer.sh
``
AMQP sequence:
``
./startContainerAMQP.sh
```

<h4>IMPORTANT</h4>
Change config in the .sh files itself. Most importantly the HOST-variable, that
has to be "docker.for.mac.localhost" for macOS instead of just "localhost".

```

NR_OF_INSTANCES => Number of subscriber instances (round robin for HTTP Requests)
MESSAGE_COUNT => Amount of single requests as JSON to send in total
MESSAGE_DELAY => Time between each request by the publisher in milliseconds
HOST => Internal host to connect between docker containers. Default for macOS "docker.for.mac.localhost", otherwise use "localhost"
```
