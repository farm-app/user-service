#!/bin/bash

IMAGE_NAME="user-service"
TAG="dev"
DOCKERHUB_NAME="andrey2702"

FULL_NAME="${DOCKERHUB_NAME}/${IMAGE_NAME}:${TAG}"

docker build -t "${FULL_NAME}" .
docker push "${FULL_NAME}"