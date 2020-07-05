#!/usr/bin/env bash

killall node
PORT=2021 node httpServer.js &
PORT=2022 node httpServer.js &
PORT=2023 node httpServer.js &
