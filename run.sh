#!/bin/bash
set -e

echo "Building application & running tests"
mvn clean verify

export DEFAULT_VIDEOS_DIRECTORY=/tmp/videos
read -p "Specify your videos directory (defaults to $DEFAULT_VIDEOS_DIRECTORY): " VIDEOS_DIRECTORY
export VIDEOS_DIRECTORY=${VIDEOS_DIRECTORY:-$DEFAULT_VIDEOS_DIRECTORY}

echo "Running application with videos directory $VIDEOS_DIRECTORY"
java -jar target/video-service.jar server src/environments/development.yml