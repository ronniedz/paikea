/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:42:59-08:00
*/

/* eslint-disable no-unused-vars */

import expect from 'expect'
import appReducer from '../reducer'
import {
  changeMainSearch, // this should test that it goes to url('/search')
  loadError,
  setVidDimensions,
} from '../actions'
import { fromJS, List } from 'immutable'
import { videoassets as vidconfig } from 'siteconfig'

const defaultviddim = vidconfig.dimensions[0]

describe('appReducer', () => {
  let state
  beforeEach(() => {
    state = fromJS({
      agegroup: [],
      authorizedby: false,
      associateVideo: {
        videoobj: {},
        options: {
          playlists: [],
        },
      },
      currentlySending: false,
      error: false,
      loading: false,
      newchild: {
        name: '',
        agegroupid: '',
      },
      path: '/',
      searchval: '',
      token: false,
      session: {},
      user: {},
      userchildren: [],
      pagedimensions: {
        height: defaultviddim.height,
        width: defaultviddim.width,
        thumbwidth: defaultviddim.thumbwidth,
      },
    })
  })

  it('should return the initial state', () => {
    const expectedResult = state
    expect(appReducer(undefined, {})).toEqual(expectedResult)
  })

  it('should handle the loadError action correctly', () => {
    const fixture = {
      msg: 'Not found',
    }
    const expectedResult = state
      .set('error', fixture)
      .set('loading', false)

    expect(appReducer(state, loadError(fixture))).toEqual(expectedResult)
  })

  it('should handle the set dimension action correctly', () => {
    const fixture = {
      dimensions: {
        upperthreshold: 640,
        height: 240,
        width: 426,
        thumbwidth: 426,
      },
    }

    const expectedResult = state
      .setIn(['pagedimensions', 'height'], fixture.dimensions.height)
      .setIn(['pagedimensions', 'width'], fixture.dimensions.width)
      .setIn(['pagedimensions', 'thumbwidth'], fixture.dimensions.thumbwidth)

    expect(appReducer(state, setVidDimensions(fixture.dimensions))).toEqual(expectedResult)
  })

  // it('should handle the loadError action correctly', () => {
  //   const fixture = {}
  //   const expectedResult = state
  //     .set('authorizedby', action.authorized)
  //
  //   expect(appReducer(state, loadError(fixture))).toEqual(expectedResult)
  // })
})
