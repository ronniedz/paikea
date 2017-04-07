/**
* @Author: gajo
* @Date:   2016-06-21T00:41:36-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-02T00:40:29-07:00
*/

import { fromJS } from 'immutable'
import expect from 'expect'

import {
  selectPageState,
} from '../selectors'

describe('selectPageState', () => {
  const homeSelector = selectPageState()
  it('should select the home state', () => {
    const homeState = fromJS({
      userData: {},
    })
    const mockedState = fromJS({
      home: homeState,
    })
    expect(homeSelector(mockedState)).toEqual(homeState)
  })
})
