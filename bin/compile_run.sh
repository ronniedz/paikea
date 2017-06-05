#!/usr/bin/env bash

##############################################################################
# compile.sh
#
# 
# This start-up script is created as a convenience for building and running the 2 servers that 
# comprise this application. The components are: a Rest Server and Camel Router
# These can run be on separate machines. The components communicate over a REST interface.
#
# - RBD [2016-10-10]
#
##############################################################################


BUILD_DIR="`dirname $0`"
CWD=$PWD
cd $BUILD_DIR
BUILD_DIR=${PWD}

PAIKEA_HOME="`dirname ${BUILD_DIR}`"
cd $PAIKEA_HOME

QUEUE_PID_FILE=${PAIKEA_HOME}/run/paikea_msgqueue.pid
RESTSRV_PID_FILE=${PAIKEA_HOME}/run/paikea_restserver.pid

QUEUE_LOG=${PAIKEA_HOME}/logs/paikea_queue.log
RESTSRV_LOG=${PAIKEA_HOME}/logs/paikea_restserver.log

dependency_dirs="utility-deps rdbms-deps"

runtime_dirs="run logs"

##
# TODO - this value should be set from the configuration file or
# passed on the command-line
QUEUE_PORT=7070
RESTSERVER_PORT=8080
CONFIG_FILE=config.yml
RETRY=5

source "$BUILD_DIR/functions.sh"

check_options $@

# function check_options ;
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


## Create logs/ and run/ directories if missing
init_dirs $runtime_dirs

stop_servers

mvn clean

compile_queue && run_queue

if is_port_listening $QUEUE_PORT $RETRY; then

	compile_rest_server && run_rest_server

	if is_port_listening $RESTSERVER_PORT ; then
		echo "	Running on \
		http://localhost:$QUEUE_PORT"
		echo "	Running on \
		http://localhost:$RESTSERVER_PORT"
	fi
fi



cd $CWD

