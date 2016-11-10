#!/usr/bin/env bash

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
mvn clean package install 2>&1 >> logs/paikea_queue.log &
QUEUE_PID=$!
echo "Starting 'Message Queue' (pid: ${QUEUE_PID}) ..."

## Check that port is update

let MAX_TRY=6
CODE=$(/bin/bash -c 'cat < /dev/null > /dev/tcp/localhost/7070 2>&1>/dev/null ; echo -n $?')
while [[ "$CODE" -ne "0" && $MAX_TRY -ne 0 ]]; do
let MAX_TRY-=1
 sleep 5
 CODE=$(/bin/bash -c 'cat < /dev/null > /dev/tcp/localhost/7070 2>&1>/dev/null ; echo -n $?')
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
	java -jar target/VidLib_RestServices-1.0.0.jar server config.yml  2>&1 >>../logs/paikea_restserver.log &
	
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
