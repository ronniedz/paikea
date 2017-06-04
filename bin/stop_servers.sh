#!/usr/bin/env bash

if [ $# -ge 1 ];
then
	for servicen in "$@"
	do
		pidfile="run/paikea_${servicen}.pid"
		if  [ -f  "$pidfile" ];
		then
			echo "Killing $pidfile"
			kill -KILL "`cat ${pidfile}`" && echo "Stopped"
			sleep 1
			rm ${pidfile}
		fi
	done
else
	RUNPID=$(ls ./run/*.pid 2>/dev/null || false)
	if [ "$RUNPID" ]; then
		for pidfile in ${RUNPID[@]};
		do
			echo "Killing $pidfile"
			kill -KILL "`cat ${pidfile}`" && echo "Stopped"
			sleep 1
			rm ${pidfile}
		done
	fi	
fi