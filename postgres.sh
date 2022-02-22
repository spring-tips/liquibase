#!/usr/bin/env bash

NAME=${1}-postgres
PORT=${2:-5432}

docker run -d  --name $NAME \
 -p ${PORT}:5432 \
 -e POSTGRES_USER=user \
 -e PGUSER=user \
 -e POSTGRES_PASSWORD=pw \
 postgres:latest
