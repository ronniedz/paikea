/**
 * @Author: gajo
 * @Date:   2016-06-19T21:34:03-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T01:00:14-08:00
 */

/*
 * App Actions
 *
 * Actions change things in your application
 * Since this boilerplate uses a uni-directional data flow, specifically redux,
 * we have these actions which are the only way your application interacts with
 * your appliction state. This guarantees that your state is up to date and nobody
 * messes it up weirdly somewhere.
 *
 * To add a new Action:
 * 1) Import your constant
 * 2) Add a function like this:
 *    export function yourAction(var) {
 *        return { type: YOUR_ACTION_CONSTANT, var: var }
 *    }
 */

import {
  CHANGE_MAIN_SEARCH,
  CLEAR_ERROR,
  CREATE_CHILD,
  CREATE_CHILD_SUCCESS,
  LOAD_ERROR,
  RETRIEVE_AGEGROUP,
  RETRIEVE_AGEGROUP_SUCCESS,
  SENDING_REQUEST,
  SET_AUTHORIZED_BY,
  SET_USER,
  SET_USER_CHILDREN,
  SET_VID_DIMENSIONS,
  SET_VIDEO_MODE,
} from './constants'

/**
 * Changes the input field of the form
 *
 * @param  {searchval} searchval The new text of the input field
 *
 * @return {object}    An action object with a type of CHANGE_MAIN_SEARCH
 */
export function changeMainSearch(searchval) {
  return {
    type: CHANGE_MAIN_SEARCH,
    searchval,
  }
}

/**
* Sets the `error` state as empty
*/
export function clearError() {
  return {
    type: CLEAR_ERROR,
  }
}

export function createChild(name, agegroupid) {
  return {
    type: CREATE_CHILD,
    name,
    agegroupid,
  }
}

export function createChildSuccess(result) {
  return {
    type: CREATE_CHILD_SUCCESS,
    result,
  }
}

/**
 * Dispatched when loading the search responses fails
 *
 * @param  {object} error The error
 *
 * @return {object}       An action object with a type of LOAD_REPOS_ERROR passing the error
 */
export function loadError(error) {
  return {
    type: LOAD_ERROR,
    error,
  }
}

export function retrieveAgeGroup() {
  return {
    type: RETRIEVE_AGEGROUP,
  }
}

export function ageGroupLoaded(results) {
  return {
    type: RETRIEVE_AGEGROUP_SUCCESS,
    results,
  }
}

/**
 * Sets the `currentlySending` state, which displays a loading indicator during requests
 * @param  {boolean} sending True means we're sending a request, false means we're not
 */
export function sendingRequest(sending) {
  return {
    type: SENDING_REQUEST,
    sending,
  }
}

export function setAuthorizedBy(authorized, path) {
  return {
    type: SET_AUTHORIZED_BY,
    authorized,
    path,
  }
}

export function setUser(user) {
  return {
    type: SET_USER,
    user,
  }
}

export function setUserChildren(children) {
  return {
    type: SET_USER_CHILDREN,
    children,
  }
}

export function setVidDimensions(dimensions) {
  return {
    type: SET_VID_DIMENSIONS,
    dimensions,
  }
}

export function setVideoMode(mode) {
  return {
    type: SET_VIDEO_MODE,
    mode,
  }
}
