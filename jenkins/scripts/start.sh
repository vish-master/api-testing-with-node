#!/usr/bin/env bash
set -x
npm start &
sleep 1
echo $! > .pidfile
set +x