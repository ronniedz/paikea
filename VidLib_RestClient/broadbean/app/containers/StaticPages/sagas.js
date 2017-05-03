import { take, call, put, select, fork, cancel } from 'redux-saga/effects'
import { LOAD_HTML } from './constants'
import { selectPageContent, selectPathname } from './selectors'
import request, {
  httpOptions as head,
} from 'utils/request'
import { LOCATION_CHANGE } from 'react-router-redux'
import {
  staticpages,
} from 'siteconfig'
import {
  loadError,
} from 'containers/App/actions'
import {
  putIt,
} from '../shared'
import {
  requestHTMLLoaded,
} from './actions'

export function* loadStaticHTML() {
  while (yield take(LOAD_HTML)) {
    const pathname = yield select(selectPathname())
    const requestURL = `${staticpages.endpoint}`
    const results = yield call(request, requestURL)
    yield putIt(results.err, requestHTMLLoaded, results.data.about.content)
  }
}

/**
 * Root saga manages watcher lifecycle
 */
export function* staticHTMLData() {
  // Fork watcher so we can continue execution
  const watcher = yield fork(loadStaticHTML)

  // Suspend execution until location changes
  yield take(LOCATION_CHANGE)
  yield cancel(watcher)
}

// All sagas to be loaded
export default [
  staticHTMLData,
]
