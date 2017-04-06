/**
* @Author: gajo
* @Date:   2016-12-08T22:05:25-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T22:06:32-08:00
*/

import expect from 'expect'
import offspringReducer from '../reducer'
import { fromJS } from 'immutable'

describe('offspringReducer', () => {
  let state
  beforeEach(() => {
    state = fromJS({
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
  })
  it('returns the initial state', () => {
    const expectedResult = state
    expect(offspringReducer(undefined, {})).toEqual(expectedResult)
  })
})
