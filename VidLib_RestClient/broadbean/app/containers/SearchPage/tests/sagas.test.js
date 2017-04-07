/**
* @Author: gajo
* @Date:   2016-06-28T21:39:08-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-19T10:57:27-07:00
*/

/* eslint-disable no-unused-vars */
/* eslint-disable no-console */

/**
 * Test  sagas
 */

import expect from 'expect'
import { take, call, put, select, fork, cancel } from 'redux-saga/effects'
import { LOCATION_CHANGE } from 'react-router-redux'

import {
  fetchYTBeanSearch,
  getYTBeanWatcher,
  ytBeanData,
} from '../sagas'

import { LOAD_YT_SEARCH } from '../constants'
import { ytSearchLoaded } from '../actions'
import { loadError } from 'containers/App/actions'

import request from 'utils/request'
import { selectMainSearchVal } from 'containers/App/selectors'
import { selectNavToken } from '../selectors'

import { search } from 'siteconfig'

const testsearchval = 'children learn numbers'

describe('fetchYTBeanSearch Saga', () => {
  let getYTBeanDataGenerator

  // We have to test twice, once for a successful load and once for an unsuccessful one
  // so we do all the stuff that happens beforehand automatically in the beforeEach
  beforeEach(() => {
    getYTBeanDataGenerator = fetchYTBeanSearch()

    const selectDescriptor = getYTBeanDataGenerator.next().value
    expect(selectDescriptor).toEqual(select(selectMainSearchVal()))

    const { pagetoken } = select(selectNavToken())

    const requestURL = `${search.endpoint}?q=${search.queryprepend + testsearchval + search.defaultparams('US', 'en', pagetoken)}`

    const callDescriptor = getYTBeanDataGenerator.next(testsearchval).value
    console.log('revisit errrors: callDescriptor ...', callDescriptor)
      // expect(callDescriptor).toEqual(call(request, requestURL))
  })

  it('should dispatch the ytSearchLoaded action if it requests the data successfully', () => {
    const response = {
      data: [{
        name: 'First result',
      }, {
        name: 'Second result',
      }],
    }

    const putDescriptor = getYTBeanDataGenerator.next(response).value
    // expect(putDescriptor).toEqual(put(ytSearchLoaded(response.data, testsearchval)))
  })

  it('should call the loadError action if the response errors', () => {
    const response = {
      err: 'Some error',
    }
    const putDescriptor = getYTBeanDataGenerator.next(response).value
    // expect(putDescriptor).toEqual(put(loadError(response.err)))
  })
})


describe('getYTBeanWatcher Saga', () => {
  const getYTBeanWatcherGenerator = getYTBeanWatcher()

  it('should watch for LOAD_YT_SEARCH action', () => {
    const takeDescriptor = getYTBeanWatcherGenerator.next().value
    expect(takeDescriptor).toEqual(take(LOAD_YT_SEARCH))
  })

  it('should invoke fetchYTBeanSearch saga on actions', () => {
    const callDescriptor = getYTBeanWatcherGenerator.next(put(LOAD_YT_SEARCH)).value
    expect(callDescriptor).toEqual(call(fetchYTBeanSearch))
  })
})

describe('ytBeanDataSaga Saga', () => {
  const ytBeanDataSaga = ytBeanData()

  let forkDescriptor

  it('should asyncronously fork getYTBeanWatcher saga', () => {
    forkDescriptor = ytBeanDataSaga.next()
    expect(forkDescriptor.value).toEqual(fork(getYTBeanWatcher))
  })

  it('should yield until LOCATION_CHANGE action', () => {
    const takeDescriptor = ytBeanDataSaga.next()
    expect(takeDescriptor.value).toEqual(take(LOCATION_CHANGE))
  })

  it('should finally cancel() the forked getYTBeanWatcher saga',
    function* ytBeanDataSagaCancellable() {
      // reuse open fork for more integrated approach
      forkDescriptor = ytBeanDataSaga.next(put(LOCATION_CHANGE))
      expect(forkDescriptor.value).toEqual(cancel(forkDescriptor))
    }
  )
})
