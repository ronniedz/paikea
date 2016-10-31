#!/usr/bin/env bash

for pidfile in "run/paikea_restsrvr.pid run/paikea_msgqueue.pid"; do
	if [ -e "$pidfile" ]
	then
		kill -KILL "`cat ${pidfile}`" && rm ./run/${pidfile}
	fi
done