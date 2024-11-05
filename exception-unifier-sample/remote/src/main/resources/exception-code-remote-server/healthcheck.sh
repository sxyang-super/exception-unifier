#!/bin/bash

# Check if required parameters are provided
if [ -z "$1" ]; then
    echo "Error: No healthcheck endpoint provided."
    exit 1
fi

if [ -z "$2" ]; then
    echo "Error: No timeout provided."
    exit 1
fi

url="$1"
timeout="$2"
elapsed=0
interval=2

# Health check loop
while true; do
    echo "Checking health of exception code server..."

    response_code=$(curl --max-time "$interval" --silent --write-out "%{http_code}" --output /dev/null "$url")

    if [ "$response_code" == "200" ]; then
        echo "Exception code server is healthy."
        exit 0
    fi

    elapsed=$((elapsed + interval))

    if [ "$elapsed" -ge "$timeout" ]; then
        echo "Exception code server is unhealthy."
        exit 1
    fi

    sleep "$interval"
done
