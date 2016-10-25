A managed queue to a YouTube API client
=======================================

# System

# Install mongo
## Add mongo repo

	```
	echo "[MongoDB]
	name=MongoDB Repository
	baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64
	gpgcheck=0
	enabled=1" | sudo tee -a /etc/yum.repos.d/mongodb.repo
	```


	`sudo yum install -y mongodb-org-server mongodb-org-shell mongodb-org-tools`


## Start the services 

	`mvn clean compile camel:run`


## Configuration

	YouTube API properties are set in:
	
		`yt-gateway.properties`
		
		
	Routes are configured in 
	
		`META-INF/spring/camel.xml`

		
### Drop collections:

```
			var collectionNames = db.getCollectionNames();
			for (var i = 0, len = collectionNames.length; i < len ; i++ ) {
				var collectionName = collectionNames[i];
				if (collectionName.indexOf('vcache_') == 0) {
					db[collectionName].drop()
				}
			}
			```

		`  db.cache_control_dat.drop()`

______
