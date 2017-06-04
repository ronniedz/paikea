# Project Paikea 

An application to make it easier for parents and guardians to use youtube for their kids.

## Overview

The application was broken into several components for... modularity in development, the ability to process video objects (JSON) in a pipeline, ease of adding features and scalability.

### Components:
 
  - VidLib [Rest Client](VidLib_RestClient/README.md)
  - VidLib [Rest Server](VidLib_RestServices/README.md)
  - A Youtube web agent
  - A Camel router used as a pipeline for video results
 
_____

## Dependencies

### Remote

 + You will need to setup a [YouTube Data API v3](https://console.developers.google.com/apis/) project.
	- Once setup, configure the Google client Id in [config.yml](VidLib_RestServices/config.yml)

 + Authentication via Google SSO.
	- While logged into [Google Console], register this app's domain as an Authorized JavaScript origin (more here: https://developers.google.com/identity/sign-in/web/devconsole-project).
	- These endpoints exist for callbacks
		- `POST: /api/auth/gcallback`	- Login
		- `POST: /api/auth/revoke`		- Logout
		- `GET: /api/auth/logout`		- Logout

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
 + Install Chromium for Karma clientside testing
 	- https://chromium.googlesource.com/chromium/src/+/lkcr/docs/linux_build_instructions.md
 		
 + **Postponed ...** Neo4J
 	- For recommendations, user interaction feedback, as an alternative to RDBMS join traversals. This will support the rating/voting type queries.

 + Nginx - Serves the client. Requests to [domain]/api/ are proxied to the Rest Server
	- see [Config](httpd_conf/nginx.conf)
	- Port `81` (as of writing)

------
 + Camel. An internal service queue. More [info here](VidLib_Youtube_pipes/README.md)
	- Port `7070` - internal Rest server
	- **Postponed ...**
		- ActiveMQ - no throttling needed (yet)
		- Port `61616`



# Run it
______


## Get Source
```
#!bash
git clone git@bitbucket.org:dietary_builders/youtube-rider.git [destination]
```
*(You might need to create a log/ directory)*

## Configure

1. Complete configuration of [config.yml](VidLib_RestServices/config.yml)
2. Serving the client-side application. Configure nginx (or other)
  -  A file such as the following should be **Include**d in `/etc/nginx.conf` [Config](httpd_conf/nginx.conf)
3. Web Client Integration 
     - The client code is compiled into `assets/`, the docroot in `nginx.conf`
     - [Rest Client README](VidLib_RestClient/README.md) for dependencies

## Build and run
Execute:

 1. This will install libraries and compile the source:
 	- `./build_run.sh`
 + Stop the services with:
	- `./stop_servers.sh`