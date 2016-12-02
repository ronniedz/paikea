# VidLib_Youtube_pipes

_A queue for coordinating requests and responses to & fro: YouTube, MongoDB and Neo4J_

## Setup Environment

### Install mongo
```
#!bash

# Add and enable Mongo repository
echo "[MongoDB]
name=MongoDB Repository
baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64
gpgcheck=0
enabled=1" | sudo tee -a /etc/yum.repos.d/mongodb.repo

# Install required packages

sudo yum install -y mongodb-org-server mongodb-org-shell mongodb-org-tools
```

### Start back-backend services:
```
#!bash
mvn clean compile camel:run &
```

## MongoDB admin

### Drop collections

```
#!javascript

// drop query result cache
var collectionNames = db.getCollectionNames();
for(var i = 0, len = collectionNames.length; i < len ; i++){
	var collectionName = collectionNames[i];
	if (collectionName.indexOf('vcache_') == 0) {
		db[collectionName].drop()
	}
}

// drop cache's metadata
db.cache_control_dat.drop()

```

## _More (WIP)_

  _Applying graphed and statistical or inferred data; **Backchat**_

### Mongo

- Inject videos' [selected] Genres
- ?? 

### GraphDB

  -  `User -[assigns] -> Genres (1 & ?2) -[TO]-> video` count as "Votes"
  -  Capture `child -[watches]-> video`
  -  Recommend videos to User2 [a parent]
	- pseudocode..
```
#!cypher
user^1 --parent-> (c1:Child)
user^2 --parent-> (c2:Child)
MATCH (Child: c) where{c1.ageRange == c2.ageRange} 
MATCH (Playlist: p) where{ p.id IN COLLECT(c1.playlists) }
RETURN COLLECT(p.videos)
```
  -   ??
  -   ??

----
## Dev environment set up

### OSX

_From https://gist.github.com/subfuzion/9630872 :_

## Install with Homebrew

    brew install mongodb

Set up launchctl to auto start `mongod`

    $ ln -sfv /usr/local/opt/mongodb/*.plist ~/Library/LaunchAgents

`/usr/local/opt/mongodb/` is a symlink to `/usr/local/Cellar/mongodb/x.y.z` 

You can use launchctl to start and stop `mongod`

    $ launchctl load ~/Library/LaunchAgents/homebrew.mxcl.mongodb.plist
    $ launchctl unload ~/Library/LaunchAgents/homebrew.mxcl.mongodb.plist
    
You can also more conveniently use `brew` to start, stop, and verify service status

    $ brew services list | grep mongodb
    $ brew services start mongodb
    $ brew services stop mongodb

#### Notes

`--dbpath` is:  /usr/local/var/mongodb

The default plist provided by homebrew stores the mongod configuration at `/usr/local/etc/mongod.conf`. This configuration specifies the `dbpath` to be `/usr/local/var/mongodb` instead of the default `/data/db`.

For more about `launchctl` see:

https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man1/launchctl.1.html#//apple_ref/doc/man/1/launchctl

http://launchd.info/

  


