#!/usr/bin/env bash

##############################################################################
# functions.sh
#
# function checkOptions ;
# die() ;
# function init_dirs ;
# function build_client ;
# function compile_queue ;
# function run_queue ;
# function is_port_listening ;
# function compile_rest_server ;
# function run_rest_server ;
# function stop_servers ;
# function install_deps ;
#
# - RBD [2016-10-10]
#
##############################################################################

usage ()
{
cat <<ENDOFMESSAGE
Usage:
	$0 [-m <component_name> ] [ -q <queue_port> ] [ -r < <rest_port> ] [ -f <config_path> ]

DESCRIPTION

	The following options are available:

	-h	This help message
	-q	Port number for message queue. Defaults to 7070
	-r	Port number for ReST server. Defaults to 8080
	-f	Location of Rest server configuration file. Default to restserver_home/config.yml

	-m=component_name
		List of: client, restserver, queue
		Default is all

ENDOFMESSAGE
}

function check_options {
	while getopts :hm:q:r:f: opt; do
	  case $opt in
	  h)
		usage
		exit 1
		;;
	  m)
		COMPONENT=$OPTARG
		;;
	  q)
		QUEUE_PORT=$OPTARG
		echo "QUEUE_PORT=${OPTARG}"
		;;
	  r)
		# Rest server PORT
		RESTSERVER_PORT=$OPTARG
		;;
	  f)
		# Rest server config file
		CONFIG_FILE=$OPTARG
		;;
	  esac
	done
}

function init_dirs {
	for wrkd in $runtime_dirs
	do
		if [ ! -d "$wrkd" ]
		then
			mkdir "$wrkd" && echo "Created directory $wrkd"
		fi
	done
}

function build_client {
    cd "${PAIKEA_HOME}/VidLib_RestClient/broadbean/"
    echo "Executing 'npm i' ..."
    echo ""
    npm i
    echo "Executing 'npm run build' ..."
    echo ""
    npm run build
    cd -
}



## compile queue
function compile_queue {
    mvn clean install -pl VidLib_Youtube_pipes -am
	let RESPCODE=$?
	if [[ $RESPCODE -gt 0 ]] ; then
		echo "Failed to compile."
	else
		echo "Camel Router compiled."
	fi
	return $RESPCODE
}

## run queue
function run_queue {
    mvn camel:run -pl VidLib_Youtube_pipes 2>&1 >> $QUEUE_LOG &
    let QUEUE_PID=$!
    echo -n $QUEUE_PID > $QUEUE_PID_FILE
	echo "Starting 'Camel Router' (pid: ${QUEUE_PID}) ..."

	## Store PID
	return $QUEUE_PID
}

## Check that port is updated
function is_port_listening {
	let port_num=$1
	let max_tries=4
    if  [ ! -z $2 ]; then
        max_tries=$2
    fi
	let nap_len=$(($max_tries+1))
	NET_CMD="cat < /dev/null > /dev/tcp/localhost/${port_num} > /dev/null ; echo -n \$?"
	echo "Checking port... ${port_num}"

	let RESPCODE=$(/bin/bash -c "$NET_CMD")
	while [[ "$RESPCODE" -ne "0" && $max_tries -ne 0 ]]; do
		echo "Waiting ${nap_len} seconds before retrying."
		let max_tries-=1
		let nap_len+=1
		sleep $nap_len
		echo "Retrying... ${max_tries} more times"
		RESPCODE=$(/bin/bash -c "$NET_CMD")
	done
	
	let max_tries=1

	while [[ "$RESPCODE" -ne "0" && $max_tries -ne 0 ]]; do
		echo "Final try. Waiting 4 seconds before trying ..."
		sleep 4
		RESPCODE=$(/bin/bash -c "$NET_CMD")
		let max_tries-=1
	done

	if [[ $RESPCODE -gt 0 ]]; then
		echo "No response from ${port_num}. Quitting."	
	else
		echo "Connected to ${port_num} !"
	fi
	return $RESPCODE
}

## compile rest_server
function compile_rest_server {
    mvn package -pl VidLib_RestServices -am
	let RESPCODE=$?
	if [[ $RESPCODE -gt 0 ]] ; then
		echo "Failed to compile."
	else
		echo "Rest Server compiled."
	fi
	return $RESPCODE
}

## run rest_server
function run_rest_server {
    cd "${PAIKEA_HOME}/VidLib_RestServices"

    jar_file=$(find ./target -name VidLib_RestServices\*.jar | grep -v 'source')
    if [[ ! -z $jar_file ]]; then
        java -Dfile.encoding=UTF-8 -jar $jar_file server $CONFIG_FILE 2>&1 >> $RESTSRV_LOG &
        RSERVER_PID=$!

        echo "Starting Rest Server (pid: ${RSERVER_PID}) - Running!"

        ## Store PID
        echo -n ${RSERVER_PID} > $RESTSRV_PID_FILE

    fi
    cd -
}

function stop_servers {
    RUNPID=$(ls ./run/*.pid 2>/dev/null || false)

    if [ "$RUNPID" ]; then
        for pidfile in ${RUNPID[@]};
        do
            echo "Shutting down process $pidfile"
            tpid="`cat ${pidfile}`"
            kill -KILL $tpid && rm ${pidfile} && echo "Stopped $tpid"
            sleep 1
        done
    fi
}

function install_deps {
	prev=$PWD
	cd $PAIKEA_HOME
	## Install basic dependencies
	for compdir in ${dependency_dirs};
	do
		cd $compdir
		mvn clean install
		cd ..
	done
	cd $prev
}

