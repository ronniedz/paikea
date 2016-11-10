# VidLib_Youtube_pipes

_A queue for managing YouTube and backend requests_

# Setup

### Install mongo

1. Add and enable Mongo repository for YuM :

		echo "[MongoDB]
		name=MongoDB Repository
		baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64
		gpgcheck=0
		enabled=1" | sudo tee -a /etc/yum.repos.d/mongodb.repo


2. Install the packages:

		sudo yum install -y mongodb-org-server mongodb-org-shell mongodb-org-tools

----
### Route Configuration

   - YouTube API properties: `yt-gateway.properties`
     
   - Routes are configuration: `META-INF/spring/camel.xml`

----
### Start back-backend services:

		mvn clean compile camel:run &

----
###  Application DB admin - MongoDB

### Drop collections:

1. To reset collections:

		var collectionNames = db.getCollectionNames();
		for (var i = 0, len = collectionNames.length; i < len ; i++ ) {
			var collectionName = collectionNames[i];
			if (collectionName.indexOf('vcache_') == 0) {
				db[collectionName].drop()
			}
		}

2. Clear video collection search-metadata.

		db.cache_control_dat.drop()

# _More (WIP)_

  _Application of graphed or statistical data; Backchat - interactions sent to backend._

## Mongo

1. Inject videos' [selected] Genres
2. ?? 

## GraphDB

1. `User -[assigns] -> Genres (1 & ?2) -[TO]-> video` count as "Votes"
2. Capture `child -[watches]-> video`
3. Recommend videos to User2 [a parent]
  - user^1 --parent-> (c1:Child)
  - user^2 --parent-> (c2:Child)
  - MATCH (Child: c) where{c1.ageRange == c2.ageRange} 
  - MATCH (Playlist: p) where{ p.id IN COLLECT(c1.playlists) }
  - RETURN COLLECT(p.videos)
4.  ??
5.  ??



