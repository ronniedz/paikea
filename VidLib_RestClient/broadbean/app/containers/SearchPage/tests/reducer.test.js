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
/*
  it('should push youtube search result to search historry collection', () => {
    const fixture = [{
      results: 'YT search results',
    }]
    const searchval = 'children learn math'
    const expectedResult = state
      .setIn(['ytSearch', 'results'], fixture)
      .set('loading', false)
      .set('searchval', searchval)

    expect(searchReducer(state, ytSearchLoaded(fixture, searchval))).toEqual(expectedResult)
  })

  */
})
