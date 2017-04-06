## Latest Version

`/broadbean`

## Quick Start - Server Dev Environment

refer to `/README.md` to build and run the java server

note: currently, you will get a not found request for recommendations at port 4001, unless you have the client dev environment running.

## Build the Rest Client App

```
cd /broadbean
npm run build
```

## Develop the Rest Client App under the Java Server with watched rebuilds

This is good for when developing the client app along with the local Java Server for debugging authentications, sessions, etc.

```
cd /broadbean
npm run build:watch
```
