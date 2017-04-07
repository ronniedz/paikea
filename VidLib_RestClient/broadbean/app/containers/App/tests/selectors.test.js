/**
* @Author: gajo
* @Date:   2016-06-23T01:12:29-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-29T10:24:39-07:00
*/

import { fromJS } from 'immutable'
import expect from 'expect'

import {
  selectMainSearchVal,
  selectGlobal,
  selectLoading,
  selectError,
  selectLocationState,
} from '../selectors'


describe('selectMainSearchVal', () => {
  const searchvalSelector = selectMainSearchVal()
  it('should select the main search value', () => {
    const searchval = 'children learn alphabet'
    const mockedState = fromJS({
      global: {
        searchval,
      },
    })
    expect(searchvalSelector(mockedState)).toEqual(searchval)
  })
})

describe('selectGlobal', () => {
  const globalSelector = selectGlobal()
  it('should select the global state', () => {
    const globalState = fromJS({})
    const mockedState = fromJS({
      global: globalState,
    })
    expect(globalSelector(mockedState)).toEqual(globalState)
  })
})

describe('selectLoading', () => {
  const loadingSelector = selectLoading()
  it('should select the loading', () => {
    const loading = false
    const mockedState = fromJS({
      global: {
        loading,
      },
    })
    expect(loadingSelector(mockedState)).toEqual(loading)
  })
})

describe('selectError', () => {
  const errorSelector = selectError()
  it('should select the error', () => {
    const error = 404
    const mockedState = fromJS({
      global: {
        error,
      },
    })
    expect(errorSelector(mockedState)).toEqual(error)
  })
})

describe('selectLocationState', () => {
  const locationStateSelector = selectLocationState()
  it('should select the route as a plain JS object', () => {
    const route = fromJS({
      locationBeforeTransitions: null,
    })
    const mockedState = fromJS({
      route,
    })
    expect(locationStateSelector(mockedState)).toEqual(route.toJS())
  })
})
