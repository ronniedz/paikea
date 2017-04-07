/**
* @Author: gajo
* @Date:   2016-06-28T23:05:25-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-17T01:33:12-07:00
*/

/*
 *
 * Search actions
 *
 */

import {
  ADD_VIDEO,
  LOAD_YT_SEARCH,
  LOAD_YT_SEARCH_SUCCESS,
  CHANGE_VIDEO,
  IS_LOOPING,
} from './constants'

export function addToPlaylist(set, playlists) {
  return {
    type: ADD_VIDEO,
    set,
    playlists,
  }
}

/**
 * Load the YT search query, this action starts the request saga
 *
 * @return {object} An action object with a type of LOAD_YT_SEARCH
 */
export function loadYTSearch(navtoken = {}) {
  return {
    type: LOAD_YT_SEARCH,
    navtoken,
  }
}

/**
 * Dispatched when the search responses are loaded by the request saga
 *
 * @param  {?} searchval
 * @param  {array} results
 *
 * @return {object}      An action object with a type of LOAD_YT_SEARCH_SUCCESS passing the results
 */

export function ytSearchLoaded(results, searchval) {
  return {
    type: LOAD_YT_SEARCH_SUCCESS,
    results,
    searchval,
  }
}

export function changeVideo(listitemobj) {
  return {
    type: CHANGE_VIDEO,
    listitemobj,
  }
}

export function toggleLooping(isLooping) {
  return {
    type: IS_LOOPING,
    isLooping,
  }
}
