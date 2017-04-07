/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-17T01:40:22-07:00
*/

/**
 * Gets the repositories of the user from Github
 */

import { take, call, select, fork, cancel } from 'redux-saga/effects'
import { LOCATION_CHANGE } from 'react-router-redux'
import { reduce } from 'lodash'

import {
  LOAD_YT_SEARCH,
  ADD_VIDEO,
} from './constants'

import {
  ytSearchLoaded,
} from './actions'

import request from 'utils/request'
import { selectMainSearchVal } from 'containers/App/selectors'
import {
  selectNavToken,
  selectAddVideo,
  selectYTBeanResults,
} from './selectors'

import {
  playlist,
  search,
  video,
} from 'siteconfig'

import {
  putIt,
  httpHeaders as head,
} from '../shared'

/**
 * YouTube-bean search request/response handler
 */

function * generateSeriesCalls(targets, stem, body) {
  yield* targets.map((ea) => call(request, `${stem}/${ea}`, { body: JSON.stringify(body), ...head.patch }))
}

// TODOS: NEEDS TO BE REFACTORED TO SEARCH SAGA
export function* addSearchResultsToPlaylist() {
  while (yield take(ADD_VIDEO)) {
    const {
      videoobj,
      playlists,
    } = yield select(selectAddVideo())
    const vidids = videoobj.map(ea => ea.videoId)
    const searchdata = yield select(selectYTBeanResults())
    const addVids = reduce(searchdata.videos, (result, val) => {
      const indx = result.indexOf(val.videoId)
      if (indx >= 0) {
        // result[result.indexOf(val.videoId)] = val
        result.splice(indx, 1, val)
      }
      return result
    }, vidids)

    const addVideoToLibraryUrl = video.endpoint
    const videoAdded = yield call(request, addVideoToLibraryUrl, {
      body: JSON.stringify(addVids),
      ...head.post,
    })
    const addToPlaylistUrl = `${playlist.endpoint}/video`

    const callGenerator = generateSeriesCalls(playlists, addToPlaylistUrl, videoAdded.data)
    const resvals = []
    for (const value of callGenerator) {
      resvals.push(yield value)
    }
  }
}

export function* fetchYTBeanSearch() {
  const querysearchval = yield select(selectMainSearchVal())
  const { pagetoken } = yield select(selectNavToken())
  const regionlang = window.navigator.language.split('-')
  const requestURL = `${search.endpoint}?q=${search.queryprepend + querysearchval + search.defaultparams(regionlang[1], regionlang[2], pagetoken)}`
  const results = yield call(request, requestURL)

  yield putIt(results.err, ytSearchLoaded, results.data, querysearchval)
}

/**
 * Watches for LOAD_YT_SEARCH action and calls handler
 */
export function* getYTBeanWatcher() {
  while (yield take(LOAD_YT_SEARCH)) {
    yield call(fetchYTBeanSearch)
  }
}

/**
 * Root saga manages watcher lifecycle
 */
export function* ytBeanData() {
  // Fork watcher so we can continue execution
  const watcher = yield fork(getYTBeanWatcher)

  // Suspend execution until location changes
  yield take(LOCATION_CHANGE)
  yield cancel(watcher)
}

// Bootstrap sagas
export default [
  ytBeanData,
  addSearchResultsToPlaylist,
]
