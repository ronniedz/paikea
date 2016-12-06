#!/usr/bin/env bash


cd ../VidLib_common_api
mvn clean package install
cd ../VidLib_Youtube_agent
mvn clean package install

