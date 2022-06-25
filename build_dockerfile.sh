#!/bin/bash
docker build .-$(dirname "${BASH_SOURCE}") -t mkem114_bot:latest
