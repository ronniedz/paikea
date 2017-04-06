/**
* @Author: gajo
* @Date:   2016-07-20T08:18:10-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T21:58:21-08:00
*/

// This file contains the sagas used for async actions in our app. It's divided into
// "effects" that the sagas call (`authorize` and `logout`) and the actual sagas themselves,
// which listen for actions.

// Sagas help us gather all our side effects (network requests in this case) in one place

/* eslint-disable consistent-return */
/* eslint-disable no-console */

import { take, call, put, fork, select } from 'redux-saga/effects'
import {
  ageGroupLoaded,
  loadError,
  setUserChildren,
} from './actions'

import { get, reduce } from 'lodash'

/*
  These authentication calls represents calls that should be made to the server.
  We cannot use the mock json-server because of required calculations.
*/
import {
  ADD_VIDEO,
  CREATE_CHILD,
  RETRIEVE_AGEGROUP,
  SET_AUTHORIZED_BY,
} from './constants'

import request from 'utils/request'
// import auth from 'utils/auth'

import {
  agegroup,
  associatePlaylist,
  auth,
  playlist,
  userchildren,
} from 'siteconfig'

console.log('auth', auth)

import {
  httpHeaders as head,
} from '../shared'

import {
  selectAuthorizedBy,
  selectNewChild,
  selectAddVideo,
 } from './selectors'

import { browserHistory } from 'react-router'

import { selectYTBeanResults } from 'containers/SearchPage/selectors'

function * generateSeriesCalls(targets, stem, body) {
  yield* targets.map((ea) => call(request, `${stem}/${ea}`, { body: JSON.stringify(body), ...head.patch }))
}

// TODOS: NEEDS TO BE REFACTORED TO SEARCH SAGA
export function * addVideosToPlaylist() {
  while (yield take(ADD_VIDEO)) {
    const { videoobj, playlists } = yield select(selectAddVideo())
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

    const addToPlaylistUrl = `${playlist.endpoint}/video`

    const callGenerator = generateSeriesCalls(playlists, addToPlaylistUrl, addVids)
    const resvals = []
    for (const value of callGenerator) {
      resvals.push(yield value)
    }
  }
}

export function * handleExternalAuthenticators() {
  while (yield take(SET_AUTHORIZED_BY)) {
    const authuser = yield select(selectAuthorizedBy())
    console.log('authuser', authuser)
    if (authuser) {
      console.log('authuser.id_token', authuser.id_token)
      const formData = new URLSearchParams()
      formData.append('id_token', authuser.id_token)
      const beanToken = yield call(request, auth.endpoint, { body: formData, ...head.auth })

      console.log('beanToken', beanToken)

      // const beanLoggedIn = yield auth.beanlogin(
      //   authuser.id_token
      // )
      // console.log('beanLoggedIn', beanLoggedIn)
/*
      if (beanLoggedIn) {
        const requestURL = userchildren.endpoint
        const results = yield call(request, requestURL)

        if (!results.err) {
          yield put(setUserChildren(results.data))
        } else {
          yield put(loadError(results.err))
        }
      }
*/
    } else {
      // auth.logout()
      browserHistory.push('/')
    }
  }
}

export function * sendCreateChild() {
  while (yield take(CREATE_CHILD)) {
    const { name, agegroupid } = yield select(selectNewChild())

    // create child
    const createChildURL = `${userchildren.endpoint}?detail=true`
    const childCreated = yield call(request, createChildURL, { body: JSON.stringify([{ ageGroupId: agegroupid, name }]), ...head.post })
    const childId = get(childCreated, 'data[0].id')
    // end create child

    // create default child playlist
    const requestURL = playlist.endpoint
    const playlistCreated = yield call(request, requestURL, { body: JSON.stringify({ title: `${name}'s Default Playlist` }), ...head.post })
    const playlistId = get(playlistCreated, 'data').id
    // end create playlist

    // associate child to playlist
    const associatePlaylistURL = associatePlaylist(childId, playlistId)
    yield call(request, associatePlaylistURL, head.patch)
    // end associate playlist

    // get updated children's list
    if (!childCreated.err & !playlistCreated.err) {
      const listChildrenURL = userchildren.endpoint
      const results = yield call(request, listChildrenURL)

      if (!results.err) {
        yield put(setUserChildren(results.data))
      } else {
        yield put(loadError(results.err))
      }
    } else {
      yield put(loadError(childCreated.err || playlistCreated.err))
    }
  }
}

export function * fetchAgeGroups() {
  while (yield take(RETRIEVE_AGEGROUP)) {
    const requestURL = agegroup.endpoint
    const results = yield call(request, requestURL)
    if (!results.err) {
      yield put(ageGroupLoaded(results.data))
    } else {
      yield put(loadError(results.err))
    }
  }
}

// The root saga is what we actually send to Redux's middleware. In here we fork
// each saga so that they are all "active" and listening.
// Sagas are fired once at the start of an app and can be thought of as processes running
// in the background, watching actions dispatched to the store.
export default function * root() {
  yield fork(handleExternalAuthenticators)
  yield fork(fetchAgeGroups)
  yield fork(sendCreateChild)
  yield fork(addVideosToPlaylist)
}
