#!/bin/bash

set -xe

GIT_COMMIT=$(git rev-parse --verify HEAD)
GIT_BRANCH=$(git branch --show-current)

docker \
    build \
    --build-arg="GIT_COMMIT=${GIT_COMMIT}" \
    --build-arg="GIT_BRANCH=${GIT_BRANCH}" \
    -f ./docker/Dockerfile ./
