/**
* @Author: gajo
* @Date:   2016-06-28T21:39:08-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-17T03:20:39-07:00
*/

import expect from 'expect'

import {
  CHANGE_VIDEO,
  LOAD_YT_SEARCH,
  LOAD_YT_SEARCH_SUCCESS,
} from '../constants'

import {
  changeVideo,
  loadYTSearch,
  ytSearchLoaded,
} from '../actions'


describe('Search Page', () => {
  describe('Actions', () => {
    it('should return the correct type', () => {
      const expectedResult = {
        type: LOAD_YT_SEARCH,
        navtoken: {},
      }

      expect(loadYTSearch()).toEqual(expectedResult)
    })
  })

  it('has a type of LOAD_YT_SEARCH_SUCCESS', () => {
    const fixture = ['Test']
    const searchval = 'test'
    const expected = {
      type: LOAD_YT_SEARCH_SUCCESS,
      results: fixture,
      searchval,
    }
    expect(ytSearchLoaded(fixture, searchval)).toEqual(expected)
  })

  it('should return the correct type and the passed results', () => {
    const fixture = 'test'
    const expected = {
      type: CHANGE_VIDEO,
      listitemobj: fixture,
    }
    expect(changeVideo(fixture)).toEqual(expected)
  })
})
