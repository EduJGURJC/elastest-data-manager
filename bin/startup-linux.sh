#!/bin/bash
set -x
sudo true

OUTPUT="$(sudo sysctl -w vm.max_map_count=262144)"
OUTPUT2="$(sysctl vm.max_map_count)"
echo "New ${OUTPUT2}"

echo "Starting up using docker-compose"

docker network rm elastest
docker network create -d bridge elastest

sudo chmod a+rwx -R mysql/data

docker-compose --project-name edm   up -d
