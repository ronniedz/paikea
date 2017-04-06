/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-17T00:02:55-07:00
*/

/* eslint-disable consistent-return */

/*
 * HomeReducer
 *
 * The reducer takes care of our data. Using actions, we can change our
 * application state.
 * To add a new action, add it to the switch statement in the reducer function
 *
 * Example:
 * case YOUR_ACTION_CONSTANT:
 *   return state.set('yourStateVariable', true)
 */

import { fromJS, List } from 'immutable'
import {
  RETRIEVE_RECOMMENDATIONS,
  RETRIEVE_RECOMMENDATIONS_SUCCESS,
  CHANGE_VIDEO,
  IS_LOOPING,
} from './constants'

// The initial state of the App
const initialState = fromJS({
  loading: false,
  error: false,
  currentIndexes: {
    playlists: false,
    videos: false,
  },
  recommendations: [],
  videoobj: false,
  isLooping: false,
})

function normlizePlaylists(results) {
  if (results.length > 0) {
    return List.of(
      ...results.map((ealist) => {
        const videos = ealist.videos.map((listitem) =>
          ({
            videoId: listitem.videoId,
            title: listitem.title,
            defaultThumbnail: listitem.defaultThumb.url,
          })
        )
        return { ...ealist, videos }
      })
    )
  }
}

const homeReducer = (state = initialState, action) => {
  switch (action.type) {
    case CHANGE_VIDEO: {
      const playlistsindex = parseInt(action.listitemobj.playlists, 10)
      const itemsindex = parseInt(action.listitemobj.videos, 10)
      return state
      .setIn(['currentIndexes', 'playlists'], playlistsindex)
      .setIn(['currentIndexes', 'videos'], itemsindex)
      .set('videoobj', state.get('recommendations').toJS()[playlistsindex].videos[itemsindex])
    }
    case RETRIEVE_RECOMMENDATIONS:
      return state
        .set('loading', true)
    case RETRIEVE_RECOMMENDATIONS_SUCCESS: {
      const playlists = List(normlizePlaylists(action.results))
      const playlistsindex = state.getIn(['currentIndexes', 'playlists'])
      const itemsindex = state.getIn(['currentIndexes', 'videos'])
      return state
      .set('recommendations', playlists)
      .set('videoobj', playlistsindex !== false && itemsindex !== false ? playlists.get(playlistsindex).videos[itemsindex] : false)
      .set('loading', false)
    }
    case IS_LOOPING:
      return state
        .set('isLooping', action.isLooping)
    default:
      return state
  }
}

export default homeReducer
