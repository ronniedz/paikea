/**
* @Author: gajo
* @Date:   2016-12-08T22:05:25-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T01:05:50-08:00
*/

import expect from 'expect'

import {
  CHANGE_VIDEO,
  RETRIEVE_PLAYLISTS,
  RETRIEVE_PLAYLISTS_SUCCESS,
} from '../constants'

import {
  changeVideo,
  playlistsLoaded,
  retrievePlaylists,
} from '../actions'

describe('Offspring Page', () => {
  describe('Unary Actions', () => {
    const testcases = [
      [playlistsLoaded, 'results', RETRIEVE_PLAYLISTS_SUCCESS],
      [retrievePlaylists, 'childid', RETRIEVE_PLAYLISTS],
    ]

    for (const test of testcases) {
      const fixture = 'fixture'
      const [func, prop, action] = test
      const expectedResult = { type: action }
      if (prop) expectedResult[prop] = fixture
      expect(func.call(this, fixture)).toEqual(expectedResult)
    }
  })

  describe('Actions', () => {
    it('should return the correct type and the passed results', () => {
      const fixture = {
        listitemobj: 'foo',
        offspringId: 'foo',
      }
      const expected = {
        type: CHANGE_VIDEO,
        listitemobj: fixture.listitemobj,
        offspringId: fixture.offspringId,
      }
      expect(changeVideo(fixture.listitemobj, fixture.offspringId)).toEqual(expected)
    })
  })
})
