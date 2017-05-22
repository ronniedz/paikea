/**
* @Author: gajo
* @Date:   2016-12-08T10:46:54-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T21:59:40-08:00
*/

/*
 *
 * OffspringPage reducer
 *
 */

import {
 fromJS,
 List,
} from 'immutable'

import {
  CHANGE_VIDEO,
  CREATE_PLAYLIST,
  DELETE_VIDEO,
  RETRIEVE_PLAYLISTS,
  RETRIEVE_PLAYLISTS_SUCCESS,
  IS_LOOPING,
  UPDATE_OFFSPRING_PLAYLIST,
} from './constants'

const initialState = fromJS({
  currentIndexes: {
    playlists: false,
    videos: false,
    itemsLength: 0,
  },
  page: {

  },
  deletefromplaylist: {
    playlistid: false,
    videoid: false,
  },
  error: false,
  loading: false,
  newplaylist: {
    title: '',
    childid: false,
  },
  childid: false,
  childplaylists: [],
  videoobj: false,
  isLooping: false,
})
import { uniqBy } from 'lodash'

function normlizePlaylists(res, append = []) {
  return List.of(
    ...res.playlists.map((ealist) => {
      const { videos, ...others } = ealist
      return {
        title: ealist.title,
        videos: videos.map((listitem) => (
          {
            videoId: listitem.videoId,
            title: listitem.title,
            defaultThumbnail: listitem.defaultThumbnail,
          })
        ),
        ...others,
      }
    })
  )
}

function offspringReducer(state = initialState, action) {
  switch (action.type) {
    case CHANGE_VIDEO: {
      const playlistsindex = parseInt(action.listitemobj.playlists, 10)
      const itemsindex = parseInt(action.listitemobj.videos, 10)
      return state
        .setIn(['page', action.offspringId, 'currentIndexes', 'playlists'], playlistsindex)
        .setIn(['page', action.offspringId, 'currentIndexes', 'videos'], itemsindex)
        .setIn(['currentIndexes', 'playlists'], playlistsindex)
        .setIn(['currentIndexes', 'videos'], itemsindex)
        .setIn(['currentIndexes', 'itemsLength'], state.get('childplaylists').toJS()[playlistsindex].videos.length)
        .setIn(['currentIndexes', 'itemsLength'], state.get('childplaylists').toJS()[playlistsindex].videos.length)
        .set('videoobj', state.get('childplaylists').toJS()[playlistsindex].videos[itemsindex])
    }
    case CREATE_PLAYLIST:
      return state
        .setIn(['newplaylist', 'title'], action.title)
        .setIn(['newplaylist', 'childid'], action.child)
    case DELETE_VIDEO:
      return state
        .setIn(['deletefromplaylist', 'playlistid'], action.playlistid)
        .setIn(['deletefromplaylist', 'videoid'], action.videoid)
    case RETRIEVE_PLAYLISTS:
      return state
        .set('childid', action.childid)
        .set('loading', true)
    case RETRIEVE_PLAYLISTS_SUCCESS: {
      const playlists = normlizePlaylists(action.results)
      let playlistsindex = false
      let itemsindex = false
      if (state.getIn(['page', state.get('childid')])) {
        playlistsindex = state.getIn(['page', state.get('childid'), 'currentIndexes', 'playlists'])
        itemsindex = state.getIn(['page', state.get('childid'), 'currentIndexes', 'videos'])
      }
      return state
        .setIn(['page', state.get('childid'), 'currentIndexes', 'playlists'], playlistsindex)
        .setIn(['page', state.get('childid'), 'currentIndexes', 'videos'], itemsindex)
        .setIn(['currentIndexes', 'playlists'], playlistsindex)
        .setIn(['currentIndexes', 'videos'], itemsindex)
        .set('childplaylists', playlists)
        .set('videoobj',
          (playlistsindex !== false && itemsindex !== false) ?
            playlists.get(playlistsindex).videos[itemsindex]
            : false
          )
        .set('loading', false)
    }
    case UPDATE_OFFSPRING_PLAYLIST:
      return state
        .set('childplaylists', action.updated)
    case IS_LOOPING:
      return state
        .set('isLooping', action.isLooping)
    default:
      return state
  }
}

export default offspringReducer
