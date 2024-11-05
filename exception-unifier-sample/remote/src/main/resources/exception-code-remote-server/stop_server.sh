#!/bin/bash

# Check if port is provided
if [ -z "$1" ]; then
    echo "Error: Port is absent."
    exit 1
fi

port="$1"

# Find the process listening on the specified port and kill it
pid=$(lsof -i :"$port" -t)

if [ -n "$pid" ]; then
    kill -9 "$pid"
    echo "Stopped the server listening on port $port successfully."
else
    echo "No process found listening on port $port."
fi

exit 0
