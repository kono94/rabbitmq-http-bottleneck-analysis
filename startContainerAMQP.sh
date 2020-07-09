#!/bin/zsh
# Get to this file location
cd "${0%/*}"

RED="\033[0;31m"
CYAN="\033[0;36m"
NC="\033[0m" # No Color
echo "${RED} ############# STARTING INSTANCES ###########"
docker rm -f subscriber-node-rabbit-{1..10}
docker rm -f publisher-java-rabbit
docker rm -f subscriber-node-http-{1..10}
docker rm -f publisher-java-http
cd subscriber-node-rabbit

NR_OF_INSTANCES=3
RABBIT_HOST="docker.for.mac.localhost"
RABBIT_USER="admin"
RABBIT_PW="admin"
MESSAGE_COUNT=10000
MESSAGE_DELAY=1
START_PARAMETERS="$RABBIT_HOST $RABBIT_USER $RABBIT_PW"

for i in $(seq 1 $NR_OF_INSTANCES)
  do
    echo "${CYAN} Instanciating node amqp subscriber instance ${i} ${NC}"
    INST=${i}
    PORT=202${INST}
    docker build -t subscriber-node-rabbit .
    docker run -d --name subscriber-node-rabbit-${INST} -e PORT=${PORT} -e RABBIT_HOST=${RABBIT_HOST} -e RABBIT_USER=${RABBIT_USER} -e RABBIT_PW=${RABBIT_PW}  -p ${PORT}:${PORT} --cpus=0.2 -it subscriber-node-rabbit
  done

echo "${RED} Waiting 5 seconds for subscribers to start up..."
sleep 5
cd ../publisher-java-rabbit
echo "${CYAN} Starting java amqp / rabbitMQ publisher ${NC}"
docker build -t publisher-java-rabbit .
# rabbitMQ connection info; host, username, password

docker run --name publisher-java-rabbit --cpus=0.2  -e MESSAGE_COUNT=${MESSAGE_COUNT} -e MESSAGE_DELAY=${MESSAGE_DELAY} -it publisher-java-rabbit "$START_PARAMETERS"
