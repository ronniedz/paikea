/**
* @Author: gajo
* @Date:   2016-11-11T01:06:52-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:56:37-08:00
*/

/* eslint-disable no-console */

import {
  changeMainSearch,
  createChild,
  loadError,
} from 'containers/App/actions'

import {
  selectAgeGroup,
  selectAuthorizedBy,
  selectMainSearchVal,
  selectUserChildren,
  selectVideoDimensions,
} from 'containers/App/selectors'

import { put } from 'redux-saga/effects'
import { push } from 'react-router-redux'

export const putIt = (err, action, ...args) => {
  if (!err) return put(action.apply(null, args))
  return put(loadError(err))
}

export const dispatches = (dispatch) => ({
  onChangeMainSearch: evt => dispatch(changeMainSearch(evt.target.value)),
  onCreateChild: (name, agegroupid) => {
    dispatch(createChild(name, agegroupid))
  },
  onNavigateChildList: (evt) => {
    evt.preventDefault()
    dispatch(push(`/offspring/${evt.target.dataset.id}`))
  },
  changeRoute: (url) => dispatch(push(url)),
})

export const stateprops = {
  agegroup: selectAgeGroup(),
  authby: selectAuthorizedBy(),
  searchval: selectMainSearchVal(),
  userchildren: selectUserChildren(),
  viddim: selectVideoDimensions(),
}
