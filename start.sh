export DOCKER_CLIENT_TIMEOUT=600
export COMPOSE_HTTP_TIMEOUT=600
sudo docker-compose up -d --no-deps --build
docker builder prune