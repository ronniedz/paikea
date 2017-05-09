# BeanCab ReST Server

## Overview

* This server, a component of Paikea, is built using **Dropwizard (ver 1.0.0-rc3)**.

* The core application entities are persisted in MySql/MariaDB

* ORM is hibernate

* Configuration
   - config.yml
      - Set Google Client API key

## Install dependencies
 
**Postponed ...**...
```
sudo yum install neo4j
```
```
#!bash
sudo service neo4j start
```
**(the dependency might still exist)*


# Run it.

```
#!bash
mvn clean package && java -jar target/VidLib_RestServices-1.0.0-sources.jar server config.yml
```