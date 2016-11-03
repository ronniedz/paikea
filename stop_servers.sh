#!/usr/bin/env bash
pidfiles="run/paikea_restsrvr.pid run/paikea_msgqueue.pid"
for pidfile in $pidfiles
do
	if [ -e "$pidfile" ]
	then
		echo "Killing $pidfile"
		kill -KILL "`cat ${pidfile}`" && rm ${pidfile} && echo "Stopped"
		sleep 1
	fi
done