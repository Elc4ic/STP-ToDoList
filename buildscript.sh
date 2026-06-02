export DOCKER_HOST="ssh://root@85.137.167.193"
docker-compose up -d --build --force-recreate --no-deps todo
docker image prune -f