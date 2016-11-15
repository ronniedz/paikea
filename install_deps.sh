#!/usr/bin/env bash

## Install basic dependencies

cd utility-deps/
mvn clean install
cd ../rdbms-deps/
mvn clean install
cd ..
