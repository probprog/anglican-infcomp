#/!/bin/bash

GIT_COMMIT="$(git rev-parse HEAD)"

docker build -t anglican-infcomp --build-arg GIT_COMMIT=$GIT_COMMIT .
