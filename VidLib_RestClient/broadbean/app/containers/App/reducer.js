/**
 * @Author: gajo
 * @Date:   2016-06-22T14:51:05-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T01:07:30-08:00
 */

/* eslint-disable consistent-return */
/* eslint-disable no-console */
/* eslint-disable no-restricted-syntax */

/*
 * AppReducer
 *
 * The reducer takes care of our data. Using actions, we can change our
 * application state.
 * To add a new action, add it to the switch statement in the reducer function
 *
 * Example:
 * case YOUR_ACTION_CONSTANT:
 *   return state.set('yourStateVariable', true)
 */

import {
  ADD_VIDEO,
  CHANGE_MAIN_SEARCH,
  CLEAR_ERROR,
  CREATE_CHILD,
  CREATE_CHILD_SUCCESS,
  LOAD_ERROR,
  RETRIEVE_AGEGROUP,
  RETRIEVE_AGEGROUP_SUCCESS,
  SENDING_REQUEST,
  SET_AUTHORIZED_BY,
  SET_USER_CHILDREN,
  SET_VID_DIMENSIONS,
} from './constants'
import { fromJS, List } from 'immutable'
import { videoassets as vidconfig } from 'siteconfig'

const defaultviddim = vidconfig.dimensions[0]

// The initial state of the App
const initialState = fromJS({
  agegroup: [],
  authorizedby: false,
  // TODOS: NEEDS REFACTORING
  associateVideo: {
    videoobj: {},
    options: {
      playlists: [],
    },
  },
  currentlySending: false,
  error: false,
  loading: false,
  newchild: {
    name: '',
    agegroupid: '',
  },
  path: '/',
  searchval: '',
  token: {},
  session: {},
  user: {},
  userchildren: [],
  pagedimensions: {
    height: defaultviddim.height,
    width: defaultviddim.width,
    thumbwidth: defaultviddim.thumbwidth,
  },
})

const appReducer = (state = initialState, action) => {
  switch (action.type) {
    case ADD_VIDEO:
      return state
      .setIn(['associateVideo', 'videoobj'], action.set)
      .setIn(['associateVideo', 'options', 'playlists'], action.playlists)
    case CHANGE_MAIN_SEARCH:
      return state
        .set('searchval', action.searchval)
    case CLEAR_ERROR:
      return state
        .set('error', false)
    case CREATE_CHILD:
      return state
        .setIn(['newchild', 'name'], action.name)
        .setIn(['newchild', 'agegroupid'], action.agegroupid)
        .set('loading', true)
    case CREATE_CHILD_SUCCESS:
      return state
    // TODOS: NEEDS REFACTORING
    case LOAD_ERROR:
      return state
      .set('error', action.error)
      .set('loading', false)
    case RETRIEVE_AGEGROUP:
      return state
        .set('loading', true)
    case RETRIEVE_AGEGROUP_SUCCESS:
      return state
        .set('agegroup', List(action.results))
    case SENDING_REQUEST:
      return state
        .set('currentlySending', action.sending)
    case SET_AUTHORIZED_BY:
      return state
        .set('authorizedby', action.authorized)
        .set('path', action.path)
    case SET_VID_DIMENSIONS: {
      return state
        .setIn(['pagedimensions', 'height'], action.dimensions.height)
        .setIn(['pagedimensions', 'width'], action.dimensions.width)
        .setIn(['pagedimensions', 'thumbwidth'], action.dimensions.thumbwidth)
    }
    case SET_USER_CHILDREN:
      return state
        .set('userchildren', List(action.children))
    default:
      return state
  }
}

export default appReducer
