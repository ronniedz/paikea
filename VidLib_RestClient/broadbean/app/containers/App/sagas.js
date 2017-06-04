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

import { get } from 'lodash'

/*
  These authentication calls represents calls that should be made to the server.
  We cannot use the mock json-server because of required calculations.
*/
import {
  CREATE_CHILD,
  RETRIEVE_AGEGROUP,
  SET_AUTHORIZED_BY,
} from './constants'

import {
  agegroup,
  associateChildPlaylistUrl,
  auth,
  playlist,
  userchildren,
} from 'siteconfig'

import request, {
  httpOptions as head,
  authTokenHeader,
  mergeInToken,
} from 'utils/request'

import { beanToken } from 'utils/auth'

import {
  selectPath,
  selectAuthorizedBy,
  selectNewChild,
} from './selectors'
import { browserHistory } from 'react-router'
import { playlistsLoaded } from 'containers/OffspringPage/actions'
import { routes } from 'clientpaths.json'

export function * handleExternalAuthenticators() {
  while (yield take(SET_AUTHORIZED_BY)) {
    // check if authorized by external authenticator
    //

    const authuser = yield select(selectAuthorizedBy())
    const path = yield select(selectPath())
    if (authuser) {
      const formData = new URLSearchParams()
      formData.append('id_token', authuser.id_token)

      if (!beanToken()) {
        const authResult = yield call(request, auth.endpoint, { body: formData, ...head.auth })
        beanToken(authResult.data)
      }

      const childrenURL = userchildren.endpoint
      const childrenResults = yield call(request, childrenURL, { headers: authTokenHeader() })

      const agegroupURL = agegroup.endpoint
      const agegroupResults = yield call(request, agegroupURL, { headers: authTokenHeader() })

      if (!childrenResults.err) {
        yield put(setUserChildren(childrenResults.data))
      } else {
        yield put(loadError(childrenResults.err))
      }

      if (!agegroupResults.err) {
        yield put(ageGroupLoaded(agegroupResults.data))
      } else {
        yield put(loadError(childrenResults.err))
      }

      if (path.includes(routes.offspring.path)) {
        const re = new RegExp(`${routes.offspring.path}/(.*)$`)
        const childid = path.match(re)[1]
        const requestURL = `${userchildren.endpoint}/${childid}`
        const results = yield call(request, requestURL, { headers: authTokenHeader() })

        if (!results.err) {
          yield put(playlistsLoaded(results.data))
        } else {
          throw new Error(results.err)
        }
      }
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
    const childCreated = yield call(request, createChildURL, { body: JSON.stringify([{ ageGroupId: agegroupid, name }]), ...mergeInToken(head.post) })
    const childId = get(childCreated, 'data[0].id')
    // end create child

    // create default child playlist
    const playlistsURL = playlist.endpoint
    const playlistCreated = yield call(request, playlistsURL, { body: JSON.stringify({ title: `${name}'s Default Playlist` }), ...mergeInToken(head.post) })
    const playlistId = get(playlistCreated, 'data.id')
    // end create playlist

    // associate child to playlist
    const childPlaylistURL = associateChildPlaylistUrl(childId, playlistId)
    yield call(request, childPlaylistURL, mergeInToken(head.patch))
    // end associate playlist

    // get updated children's list
    if (!childCreated.err & !playlistCreated.err) {
      const listChildrenURL = userchildren.endpoint
      const results = yield call(request, listChildrenURL, { headers: authTokenHeader() })

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
}
