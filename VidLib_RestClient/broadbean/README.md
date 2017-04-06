# Quick Start - Client Dev Environment

To run the mock developmental, you must have `json-server` installed in npm globally. https://www.npmjs.com/package/json-server

```
npm install -g json-server
```

Install packages.
```
npm i
```

Run the developmental the json server and server.
```
npm run start
```

**See package.json for more development scripts, including for the build**

Navigate to localhost:5000.

# Technology Overview

This application was originally bootstrapped from [react-boilerplate](https://github.com/mxstbr/react-boilerplate). See README_react-boilerplate.md for the original README. This video gives a great quick overview, https://vimeo.com/168648012 .

Review documentation at `/docs` provided by react-boilerplate for an overview of the technologies used.

# Bean Development notes

Besides the technical documentation provided by react-boilerplate, here are some notes on how specific technologies are being used.

## React

React is an isomorphic patterned and declarative library to make components out of javascript/html/css. The component style react-boilerplate employs has each component grouped into folders containing the javascript/html/css related to it, though that is not a required pattern for react itself. Unit tests are also grouped into the component folders.

https://facebook.github.io/react/

## Redux

The gist of Redux is that it uses a reducers/store/dispatcher system in handling the state of an application. The flow generally starts with a dispatch of an action to the store, which is listening and ready to enact on the action with reducers. Reducers leverage the use of 'pure' functions, to be predictable as well as flexible. https://egghead.io/lessons/javascript-redux-the-single-immutable-state-tree

### Containers versus Components

#### gist
* containers fetch data
* components consumes the data passed to it by their containers, and renders predictable views

https://github.com/reactjs/redux/issues/756 ... https://github.com/gaearon is the author of Redux


##### directory files structures
  *folders are in npm-style, where `index.js` is main entry point for each folder structure.*

  `/tests`<br />
  `actions.js` - This contains the actions that are dispatched which are being listened to by the redux store.<br />
  `constants.js` - Redux action constants<br />
  `index.js` - These are top-level react page containers that usually handle most props assignment, event handling, and dispatches for state reducers and data fetching.<br />
  `reducers.js` - These are redux logic and functions that handle state changes and data returns<br />
  `sagas.js` - Handles asynchronous data calls with the capability of handling long-running returns. Sagas listen for constants and run predefined function to handle asynchronous data calls.<br />
  `selectors.js` - Handles the caching of state and data returns. It means to prevent repeated calls to the server, when it can retrieve the data from cache. This library is configure and forget.<br />
  `styles.css` - Component localized styling<br />

#### bean.cab containers
  - App
    - The container shell that loads other containers to form pages.
      - Holds the Header and Footer components
      - Has login logic which determines if user is logged in
      - Loads playlists if user is logged in
      - Holds global states
      - Contains 'add to library' logic
    - auth - contains fake auth server implementations as stand-in to later redirect to real server
  - HomePage - main landing page
    - Loads when route '/' is submitted
    - Loads and displays site recommendations
  - Login - form submission for logging in
  - NotFoundPage - 404s go here
  - MyLibraryPage - holds user video library and future playlists
    - Loads when route '/library/*' is submitted
    - Loads and displays users' video library and future playlists
  - Register - form submission for submitting a new user
  - SearchPage - dynamic search page with pagination
    - Loads when route '/search' is submitted
    - Contains logic which drives the search requests from YouTube bean agent and pagination
    - Processes form submission from other pages
    - Renders the view for searct results

#### bean.cab components

  - AuthForm/ - Form used by login and register container pages
  - Beanstalk/ - main component that handles the main web experience
    - AddListOfVideos/ - adds multiple videos
    - AddVideo/ - adds one video per click
    - PlaylistItem/ - renders playlists item videos for all lists
    - PlaylistList/ - renders a group of video into thumbnail views
    - NavMenu/ - holds the view for page navigation, and site-wide video search form
    - VideoPlayback/ - renders the video playback, which is a YouTube iframe API
  - Header/
  - Footer/
  - ...the rest of components are react stateless wrappers of DOM elements

## React-router

Leading routing library for React clients. https://github.com/reactjs/react-router

See `routes.js`

## SAGAs

Saga files hold asynchronous actions in central locations relative to container pages.

https://www.npmjs.com/package/redux-saga

## Webpack

Module bundler for client development, devops, and builds.
https://webpack.github.io/

### css-loader

Makes css files localizable and organizeable with component folders. In the build step, the final bundle will have the css inline into the elements.

# Additional references

## Webpack code-splitting by routes:
- https://medium.com/@puppybits/webpack-code-splitting-by-routes-92f96cf733f2#.ecrark8lj

## Redux

https://quickleft.com/blog/redux-plain-english-workflow/

https://chrome.google.com/webstore/detail/redux-devtools/lmhkpmbekcpmknklioeibfkpmmfibljd?hl=en
