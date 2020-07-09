#!/bin/zsh
# Get to this file location
cd "${0%/*}"

RED="\033[0;31m"
CYAN="\033[0;36m"
NC="\033[0m" # No Color
echo "${RED} ############# STARTING INSTANCES ###########"
docker rm -f subscriber-node-http-{1..10}
docker rm -f publisher-java-http
docker rm -f subscriber-node-rabbit-{1..10}
docker rm -f publisher-java-rabbit
cd subscriber-node-http

NR_OF_INSTANCES=3
MESSAGE_COUNT=10000
MESSAGE_DELAY=1
HOST="docker.for.mac.localhost"
START_PARAMETERS=""

for i in $(seq 1 $NR_OF_INSTANCES)
  do
    START_PARAMETERS="${START_PARAMETERS} ${HOST}:202${i}/endpoint"
    echo "${CYAN} Instanciating node http subscriber instance ${i} ${NC}"
    INST=${i}
    PORT=202${INST}
    docker build -t subscriber-node-http .
    docker run -d --name subscriber-node-http-${INST} -e PORT=${PORT}  -p ${PORT}:${PORT} --cpus=0.2 -it subscriber-node-http
  done

echo "${RED} Waiting 5 seconds for subscribers to start up..."
sleep 5
cd ../publisher-java-http
echo "${CYAN} Starting java http publisher ${NC}"
docker build -t publisher-java-http .
docker run --name publisher-java-http --cpus=0.2 -e MESSAGE_COUNT=${MESSAGE_COUNT} -e MESSAGE_DELAY=${MESSAGE_DELAY} -it publisher-java-http "$START_PARAMETERS"
