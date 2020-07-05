docker build -t publisher-java-http . 
docker run --name Publisher_java_http --cpus=0.2 publisher-java-http 

INST=1

docker build -t subscriber-node-http .
docker run -d --name subscriber-node-htttp-X -e PORT=202X  -p 202X:202X--cpus=0.2 subscriber-node-http
