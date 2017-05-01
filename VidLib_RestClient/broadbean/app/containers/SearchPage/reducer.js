/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-16T21:43:05-07:00
*/

/*
 *
 * The reducer takes care of our data. Using actions, we can change our
 * application state.
 * To add a new action, add it to the switch statement in the reducer function
 *
 * Example:
 * case YOUR_ACTION_CONSTANT:
 *   return state.set('yourStateVariable', true)
 */

import {
  CHANGE_MAIN_SEARCH,
} from 'containers/App/constants'
import {
  ADD_VIDEO,
  LOAD_YT_SEARCH,
  LOAD_YT_SEARCH_SUCCESS,
  CHANGE_VIDEO,
  IS_LOOPING,
} from './constants'
import { List, fromJS } from 'immutable'

// The initial state of the App
const initialState = fromJS({
  associateVideo: {
    videoobj: {},
    options: {
      playlists: [],
    },
  },
  currentIndexes: {
    playlists: false,
    videos: false,
  },
  loadComplete: false,
  navToken: {
    listindex: false,
    pagetoken: false,
  },
  searchHistory: [],
  videoobj: false,
  ytSearch: [],
  isLooping: false,
})

function normlizeSearchResults(key, res) {
  return {
    description: 'search results',
    videos: res.items.map((listitem) => ({
      videoId: listitem.videoId,
      title: listitem.title,
      defaultThumbnail: listitem.defaultThumbnail,
      etag: listitem.etag,
      description: listitem.description,
      publishedAt: listitem.publishedAt,
    })),
    nextpagetoken: res.nextPageToken,
    title: `${key}`,
    prevpagetoken: res.prevPageToken,
  }
}

function updateWithData(collection = [], spliceindex, append) {
  // this creates a new list everytime a query is sent without a pagetoken, so the assumption is that it is as new term query
  return spliceindex === null ? List.of(append) : collection.splice(spliceindex, 1, append)
}

const searchReducer = (state = initialState, action) => {
  switch (action.type) {
    case CHANGE_MAIN_SEARCH:
      return state
        .set('searchval', action.searchval)
        .set('loadComplete', false)
    case CHANGE_VIDEO: {
      const playlistsindex = parseInt(action.listitemobj.playlists, 10)
      const itemsindex = parseInt(action.listitemobj.videos, 10)
      return state
        .setIn(['currentIndexes', 'playlists'], playlistsindex)
        .setIn(['currentIndexes', 'videos'], itemsindex)
        .set('videoobj', state.get('searchHistory').toJS()[playlistsindex].videos[itemsindex])
    }
    case LOAD_YT_SEARCH:
      return state
        .setIn(['currentIndexes', 'playlists'], false)
        .setIn(['currentIndexes', 'videos'], false)
        .set('videoobj', false)
        .set('navToken', action.navtoken)
        .set('loading', true)
        .set('error', false)
    case LOAD_YT_SEARCH_SUCCESS: {
      console.log('action', action)
      let modstate = state
        .set('searchval', action.searchval.trim())
        .set('loading', false)
        .set('loadComplete', true)

      // wrapping searchHistory in an array so it has capabilities to grow later or
      if (action.results.items.length > 0) {
        const spliceindex = state.get('navToken').listindex ? parseInt(state.get('navToken').listindex, 10) : null

        const searchterm = state.get('searchHistory') && spliceindex ? state.get('searchHistory').get(spliceindex).title : action.searchval

        const normlres = normlizeSearchResults(searchterm, action.results)
        // if there is a nav token we retrieve the listidex which the nav by token click came from, otherwise we assume it's a new search and therefore me reset index to 0
        const playlists = updateWithData(state.get('searchHistory'), spliceindex, normlres)
        const playlistsindex = state.getIn(['currentIndexes', 'playlists'])
        const itemsindex = state.getIn(['currentIndexes', 'videos'])
        modstate = modstate
          .set('ytSearch', normlres)
          .set('searchHistory', playlists)
          .set('videoobj', playlistsindex !== false && itemsindex !== false ? playlists.get(playlistsindex).videos[itemsindex] : false)
      } else {
        modstate = modstate
          .set('ytSearch', false)
      }
      return modstate
        .set('navToken', {})
    }
    case ADD_VIDEO:
      return state
        .setIn(['associateVideo', 'videoobj'], action.set)
        .setIn(['associateVideo', 'options', 'playlists'], action.playlists)
    case IS_LOOPING:
      return state
        .set('isLooping', action.isLooping)
    default:
      return state
  }
}

export default searchReducer
