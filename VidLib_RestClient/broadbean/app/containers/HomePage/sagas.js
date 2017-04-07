/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-17T15:59:39-07:00
*/

/**
 * Gets the repositories of the user from Github
 */

import { take, call, fork, cancel } from 'redux-saga/effects'
import { LOCATION_CHANGE } from 'react-router-redux'
import { RETRIEVE_RECOMMENDATIONS } from './constants'

import {
  recommendationsLoaded,
} from './actions'

import request from 'utils/request'

import { recommendations } from 'siteconfig'

import { putIt } from '../shared'

/**
 * Watches for RETRIEVE_RECOMMENDATIONS action and calls handler
 */
export function* getBeanRecommendsWatcher() {
  while (yield take(RETRIEVE_RECOMMENDATIONS)) {
    const requestURL = `${recommendations.endpoint}`
    const results = yield call(request, requestURL)
    yield putIt(results.err, recommendationsLoaded, results.data)
  }
}

/**
 * Root saga manages watcher lifecycle
 */
export function* BeanRecommendsData() {
  // Fork watcher so we can continue execution
  const watcher = yield fork(getBeanRecommendsWatcher)

  // Suspend execution until location changes
  yield take(LOCATION_CHANGE)
  yield cancel(watcher)
}

// Bootstrap sagas
export default [
  BeanRecommendsData,
]
