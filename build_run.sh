#!/usr/bin/env bash

##############################################################################
# build_run.sh
#
# 
# This start-up script is created as a convenience for building and running the 2 servers that 
# comprise this application. The components are: a Rest Server and Camel Queue
# These can run be on separate machines. The components communicate over a REST interface.
#
# - RBD [2016-10-10]
#
##############################################################################

RUNPID=$(ls ./run/*.pid 2>/dev/null || false)

if [ "$RUNPID" ]; then
	for pidfile in ${RUNPID[@]};
	do
		echo "Killing $pidfile"
		kill -KILL "`cat ${pidfile}`" && rm ${pidfile} && echo "Stopped"
		sleep 1
	done
fi



##
# TODO - this value should be set from the configuration file or 
# passed on the command-line
QUEUE_IF_PORT=7070

MSG_b=0
MSG_created=""

wrkdirs="run logs"
for wrkd in $wrkdirs
do
    if [ ! -d "$wrkd" ]
    then
        mkdir "$wrkd"
        MSG_b=1
        MSG_created="${MSG_created} ${wrkd},"
    fi
done


## Install basic dependencies

cd utility-deps/
mvn clean install
cd ../rdbms-deps/
mvn clean install
cd ..

## Build and install queue
mvn clean install
mvn -pl VidLib_Youtube_pipes camel:run 2>&1 >> logs/paikea_queue.log &
QUEUE_PID=$!
echo "Starting 'Message Queue' (pid: ${QUEUE_PID}) ..."

## Check that port is update

let MAX_TRY=6
CODE=$(/bin/bash -c "cat < /dev/null > /dev/tcp/localhost/${QUEUE_IF_PORT} 2>&1>/dev/null ; echo -n \$?")
while [[ "$CODE" -ne "0" && $MAX_TRY -ne 0 ]]; do
let MAX_TRY-=1
 sleep 7
 CODE=$(/bin/bash -c "cat < /dev/null > /dev/tcp/localhost/${QUEUE_IF_PORT} 2>&1>/dev/null ; echo -n \$?")
 echo "Retrying... (${MAX_TRY})"
done

## Store PID
echo -n ${QUEUE_PID} > ./run/paikea_msgqueue.pid

if [ "$CODE" -eq "0" ]; then

	echo "Message Queue (pid: ${QUEUE_PID}) - Running!"
	echo "Building Rest-Server... "

	cd VidLib_RestServices

	## Build server
	mvn clean package
	echo "Build Complete!"
	
	## Start server
	java -jar target/VidLib_RestServices-1.0.0.jar server config.yml 2>&1 >> ../logs/paikea_restserver.log &
	
	RSERVER_PID=$!
	echo "Starting Rest Server (pid: ${RSERVER_PID}) - Running!"

	## Store PID
	echo -n ${RSERVER_PID} > ../run/paikea_restsrvr.pid

	cd ..

    if [ MSG_b == 1 ]
    then
        echo "$MSG_created"
    fi

fi
