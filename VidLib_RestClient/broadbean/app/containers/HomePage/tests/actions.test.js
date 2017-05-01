/**
* @Author: gajo
* @Date:   2016-06-17T23:05:06-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-10T11:46:29-07:00
*/

/* eslint-disable no-unused-vars */

import expect from 'expect'

import {
  RETRIEVE_RECOMMENDATIONS,
  RETRIEVE_RECOMMENDATIONS_SUCCESS,
  CHANGE_VIDEO,
  IS_LOOPING,
} from '../constants'


import {
  changeVideo,
  retrieveRecommendations,
  recommendationsLoaded,
  toggleLooping,
} from '../actions'

// import {
//   changeVideo,
// } from 'containers/App/actions'

describe('App - Global', () => {
  describe('Actions', () => {
    it('should return the correct type and the passed results', () => {
      const expectedResult = {
        type: RETRIEVE_RECOMMENDATIONS,
      }

      expect(retrieveRecommendations()).toEqual(expectedResult)
    })
  })

  it('should return the correct type and the passed results', () => {
    const fixture = ['Test']
    const expectedResult = {
      type: RETRIEVE_RECOMMENDATIONS_SUCCESS,
      results: fixture,
    }

    expect(recommendationsLoaded(fixture)).toEqual(expectedResult)
  })

  it('should return the correct type', () => {
    const fixture = 'Test'
    const expectedResult = {
      type: CHANGE_VIDEO,
      listitemobj: fixture,
    }

    expect(changeVideo(fixture)).toEqual(expectedResult)
  })

  it('should return looping type', () => {
    const fixture = false
    const expectedResult = {
      type: IS_LOOPING,
      isLooping: fixture,
    }

    expect(toggleLooping(fixture)).toEqual(expectedResult)
  })
})
