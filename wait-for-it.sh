#!/bin/sh
host_port=$1
shift
timeout=${2:-30}

host=$(echo $host_port | cut -d: -f1)
port=$(echo $host_port | cut -d: -f2)

while ! nc -z $host $port; do
  echo "Waiting for $host_port..."
  sleep 1
  timeout=$((timeout-1))
  if [ $timeout -le 0 ]; then
    echo "Timeout waiting for $host_port"
    exit 1
  fi
done

exec "$@"