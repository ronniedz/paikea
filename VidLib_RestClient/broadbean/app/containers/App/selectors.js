/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-08T23:08:47-08:00
*/

/**
 * The global state selectors
 */

import { createSelector } from 'reselect'

export const selectGlobal = () => (state) => state.get('global')

export const selectLoading = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('loading')
)

export const selectMainSearchVal = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('searchval')
)

export const selectError = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('error')
)

export const selectYTBeanResults = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.getIn(['ytSearch', 'results'])
)

export const selectBeanRecommendsResults = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.getIn(['recommendations', 'results'])
)

export const selectCurrentlySending = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('currentlySending')
)

export const selectAgeGroup = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('agegroup')
)

export const selectNewChild = () => createSelector(
  selectGlobal(),
  (globalState) => ({ ...globalState.get('newchild').toJS() })
)

export const selectAuthorizedBy = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('authorizedby')
)

export const selectSession = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('session')
)

export const selectUser = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('user')
)

export const selectUserChildren = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('userchildren')
)

export const selectVideoDimensions = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('pagedimensions').toJS()
)

export const selectPath = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('path')
)

export const selectVideoMode = () => createSelector(
  selectGlobal(),
  (globalState) => globalState.get('videomode')
)

export const selectLocationState = () => {
  let prevRoutingState
  let prevRoutingStateJS

  return (globalState) => {
    const routingState = globalState.get('route') // or state.route

    if (!routingState.equals(prevRoutingState)) {
      prevRoutingState = routingState
      prevRoutingStateJS = routingState.toJS()
    }

    return prevRoutingStateJS
  }
}
