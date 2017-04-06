/**
* @Author: gajo
* @Date:   2016-06-19T21:34:03-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-19T10:29:03-07:00
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
  RETRIEVE_RECOMMENDATIONS,
  RETRIEVE_RECOMMENDATIONS_SUCCESS,
  CHANGE_VIDEO,
  IS_LOOPING,
} from './constants'

/**
 * Retrieves default playlist recommendations
 *
 *
 * @return {object} An action object with a type of RETRIEVE_RECOMMENDATIONS
 */
export function retrieveRecommendations() {
  return {
    type: RETRIEVE_RECOMMENDATIONS,
  }
}

export function recommendationsLoaded(results) {
  return {
    type: RETRIEVE_RECOMMENDATIONS_SUCCESS,
    results,
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
