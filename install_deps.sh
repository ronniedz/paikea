#!/usr/bin/env bash

# mvn clean hibernate3:hbm2ddl install camel:run -pl mq 
cd utility-deps/
mvn clean install
cd ../rdbms-deps/
mvn clean install
cd ..
ls

