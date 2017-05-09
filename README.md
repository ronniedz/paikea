# Paikea 

An application to make it easier for parents and guardians to use youtube for their kids.

## System Dependencies
_____

### Servers/libraries

 + Bash, Maven 3, npm & NodeJS
	- build tools
 + Java-8 or higher
 	- runtime
 + MongoDB
 	- initial store of videos from youtube
 	- Connection properties:
 		- host=localhost
 		- port= 27017 (default)
 		- user=
 		- pass=
 		- database-name = test
 	- The relevant collections are: `cache_control_dat` and the ones prefixed with `vcache_`
  + MySQL or MariaDb
 	- For user and the application bound video library

### Postponed ... 	
 + Neo4J
 	- For recommendations, user interaction feedback, as an alternative to RDBMS join traversals. This will support the rating/voting type queries.
------

### Devices

- Jetty
	- Port `8080`
- Apache Camel. An internal service queue. More [details here](VidLib_Youtube_pipes/README.md)
	- Port `7070` - internal Rest server

### Postponed ... 	
- ActiveMQ
	- Port `61616`
------

______


## Get Source
```
#!bash
git clone git@bitbucket.org:dietary_builders/youtube-rider.git [destination]
```

## Build and run
Execute:

 1. This will install libraries and compile the source:
 	- `./build_run.sh`
 2. Stop the services with:
	- `./stop_servers.sh`