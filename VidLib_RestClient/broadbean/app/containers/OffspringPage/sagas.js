/**
* @Author: gajo
* @Date:   2016-07-02T11:33:57-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T01:05:17-08:00
*/

/* eslint-disable no-unused-vars */

import {
  take,
  takeLatest,
  call,
  put,
  select,
  cancel,
  fork,
} from 'redux-saga/effects'

import {
  playlistsLoaded,
  updateOffspringPlaylist,
} from './actions'

import {
  loadError,
  setUserChildren,
} from 'containers/App/actions'

import {
  selectUserChildren,
} from 'containers/App/selectors'

import {
  CREATE_PLAYLIST,
  DELETE_VIDEO,
  RETRIEVE_PLAYLISTS,
} from './constants'

import request, {
  authTokenHeader,
  mergeInToken,
  httpOptions as head,
} from 'utils/request'

import {
  associateChildPlaylistUrl,
  playlist,
  userchildren,
} from 'siteconfig'

import {
  selectChildId,
  selectDeleteVideoFromPlaylist,
  selectNewPlaylists,
  selectPlaylists,
} from './selectors'

import { LOCATION_CHANGE } from 'react-router-redux'

function * createPlaylist() {
  const { title, childid } = yield select(selectNewPlaylists())
  let requestURL = playlist.endpoint
  const playlistCreated = yield call(request, requestURL, { body: JSON.stringify({ title }), ...mergeInToken(head.post) })
  const playlistId = playlistCreated.data.id
  // end create playlist

  // associate child to playlist
  const associatePlaylistURL = associateChildPlaylistUrl(childid, playlistId)
  const associatedPlaylist = yield call(request, associatePlaylistURL, mergeInToken(head.patchmin))

  // const action = yield take(RETRIEVE_PLAYLISTS)
  requestURL = `${userchildren.endpoint}/${childid}`
  const results = yield call(request, requestURL, { headers: authTokenHeader() })

  if (!results.err) {
    yield put(playlistsLoaded(results.data))
    const userChildren = yield select(selectUserChildren())
    const updatedChildren =
      userChildren.update(
        userChildren.findIndex(
          item => item.id === results.data.id),
          (item) => Object.assign(item, { playlists: results.data.playlists })
        )
    yield put(setUserChildren(updatedChildren))
  } else {
    yield put(loadError(results.err))
  }
}

function * deleteVideoFromPlaylist() {
  const { playlistid, videoid } = yield select(selectDeleteVideoFromPlaylist())
  const requestURL = playlist.endpoint
  const updatedPlaylist = yield call(request, `${requestURL}/${playlistid}/v/${videoid}`, mergeInToken(head.delete))
  const existingList = yield select(selectPlaylists())
  const updatedIndex = existingList.toJS().findIndex(ea => ea.id === updatedPlaylist.data.id)
  yield put(updateOffspringPlaylist(existingList.set(updatedIndex, updatedPlaylist.data)))
}

function * fetchBeanPlaylistsFlow() {
  /*
    This is a bit of a workaround. In order to load the offspring page when landing on it, we load the offspring playlist in app/saga.
    But to newly navigate to this sub-route from another route, we need it to load in componentDidMount().
    However, componentDidMount will also get triggered when landing on this page, and no token has yet been retrieved from bean. Hence, the token conditional.
    This can certainly be improved. Maybe in SearchPage/saga.js and HomePage/saga.js, where it's possible to append new videos to offspring playlists.
   */
  const token = authTokenHeader()
  if (token) {
    const childid = yield select(selectChildId())
    const requestURL = `${userchildren.endpoint}/${childid}`
    const results = yield call(request, requestURL, { headers: authTokenHeader() })

    if (!results.err) {
      yield put(playlistsLoaded(results.data))
    } else {
      throw new Error(results.err)
      // browserHistory.push('/')
    }
  }
}

function * fetchPlaylistWatcher() {
  yield takeLatest(RETRIEVE_PLAYLISTS, fetchBeanPlaylistsFlow)
}

function * createChildWatcher() {
  yield takeLatest(CREATE_PLAYLIST, createPlaylist)
}

function * createDeleteFromPlaylistWatcher() {
  yield takeLatest(DELETE_VIDEO, deleteVideoFromPlaylist)
}

function * offspringData() {
  // Fork watcher so we can continue execution
  const watcher = yield fork(fetchPlaylistWatcher)
  const watcher2 = yield fork(createChildWatcher)
  const watcher3 = yield fork(createDeleteFromPlaylistWatcher)

  // Suspend execution until location changes
  yield take(LOCATION_CHANGE)
  yield cancel(watcher)
  yield cancel(watcher2)
  yield cancel(watcher3)
}

// All sagas to be loaded
export default [
  offspringData,
]
