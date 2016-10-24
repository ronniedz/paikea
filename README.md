# Paikea 

A tool for marshalling youtube's search results. The objective is to extract videos that would be relevant to a niché set of users, in this rendition, the context is **parents**. These components comprise the server-side of the application.


## System Dependencies
_____

### Servers/libraries
 + Java-8 and higher
 	- runtime
 + MongoDB
 	- initial store of videos from youtube
 + Apache Maven 3
	- build tool
 + MySQL or MariaDb
 	- For user and the applicaion bound video library
 + Neo4J
 	- For recommendations, user interaction feedback, as an alternative to RDBMS join traversals. This will support the rating/voting type queries.


### Devices

- Jetty
	- Port `8080`
- Apache Camel
	- Port `7777` - internal Rest server
- ActiveMQ
	- Port `61616`
______

## build

Execute:

 1. `./install_deps.sh`
 2. Start message queue with:
 	- `mvn clean package install &`
 3. Start the rest service:
	- `cd VidLib_RestServices`
	- `mvn clean package && java -jar target/VidLib_RestServices-1.0.0.jar server config.yml`