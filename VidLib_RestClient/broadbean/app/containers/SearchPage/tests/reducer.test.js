/**
* @Author: gajo
* @Date:   2016-06-28T21:39:08-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-10T22:15:12-07:00
*/

/* eslint-disable no-unused-vars */

import expect from 'expect'
import searchReducer from '../reducer'
import { fromJS } from 'immutable'
import {
  changeMainSearch,
  loadError,
} from 'containers/App/actions'

import {
  loadYTSearch,
  ytSearchLoaded,
  addToPlaylist,
  changeVideo,
  toggleLooping,
} from '../actions'


describe('searchReducer', () => {
  let state
  beforeEach(() => {
    state = fromJS({
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
  })


  it('should return the initial state', () => {
    const expectedResult = state
    expect(searchReducer(undefined, {})).toEqual(expectedResult)
  })

  it('should handle the loadYTSearch action correctly', () => {
    const expectedResult = state
      .set('loading', true)
      .set('error', false)
      .set('navToken', {})
      .setIn(['ytSearch', 'results'], false)

    expect(searchReducer(state, loadYTSearch())).toEqual(expectedResult)
  })

  it('should handle the changeMainSearch action correctly', () => {
    const fixture = 'child learning'
    const expectedResult = state.set('searchval', fixture)
    expect(searchReducer(state, changeMainSearch(fixture))).toEqual(expectedResult)
  })

  it('should handle the addToPlaylist action correctly', () => {
    const [ set, playlists ] = [ { foo: 'bar' }, ['sao', 'soetnh']]
    const expectedResult = state
      .setIn(['associateVideo', 'videoobj'], set)
      .setIn(['associateVideo', 'options', 'playlists'], playlists)
    expect(searchReducer(state, addToPlaylist(set, playlists))).toEqual(expectedResult)
  })

  it('should handle the changeVideo action correctly', () => {
    const listitemobj = {
      playlists: 1,
      videos: 1,
    }
  const searchHistory = [
    {},
    {
      "videos": [
        {},
        {
          "videoId": "2nYjGy_ZUG8",
          "title": "Hello To All The Children Of The World",
          "defaultThumbnail": "https://i.ytimg.com/vi/2nYjGy_ZUG8/default.jpg?dim=120%3A90",
          "etag": "\"m2ysk\"",
          "description": "For educational use only. Images from various sources.",
          "publishedAt": "2014-10-09T06:40:29.000Z"
        }
      ]
    }
  ]
    const playlistsindex = parseInt(listitemobj.playlists, 10)
    const itemsindex = parseInt(listitemobj.videos, 10)

    const expectedResult = state
      .setIn(['currentIndexes', 'playlists'], playlistsindex)
      .setIn(['currentIndexes', 'videos'], itemsindex)
      .set('videoobj', searchHistory[playlistsindex].videos[itemsindex])
    const newstate = searchReducer(fromJS({...state.toJS(), searchHistory }), changeVideo(listitemobj))
    expect(newstate.get('currentIndexes')).toEqual(expectedResult.get('currentIndexes'))
    expect(newstate.get('videoobj')).toEqual(expectedResult.get('videoobj'))

  })

  it('should handle the addToPlaylist action correctly', () => {
    const expectedResult = state
      .set('isLooping', true)
    expect(searchReducer(state, toggleLooping(true))).toEqual(expectedResult)
  })
})
