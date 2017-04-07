/**
* @Author: gajo
* @Date:   2016-12-08T22:23:46-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:39:11-08:00
*/

import { createSelector } from 'reselect'

/**
 * Direct selector to the offspring state domain
 */
export const selectPageState = () => state => state.get('offspring')

export const selectCurrentIndexes = () => createSelector(
  selectPageState(),
  (state) => state.get('currentIndexes').toJS()
)

export const selectDeleteVideoFromPlaylist = () => createSelector(
  selectPageState(),
  (state) => state.get('deletefromplaylist').toJS()
)

export const selectPlaylists = () => createSelector(
  selectPageState(),
  (state) => state.get('childplaylists')
)

export const selectNewPlaylists = () => createSelector(
  selectPageState(),
  (state) => state.get('newplaylist').toJS()
)

export const selectChildId = () => createSelector(
  selectPageState(),
  (state) => state.get('childid')
)

export const selectVideoObj = () => createSelector(
  selectPageState(),
  (state) => state.get('videoobj')
)

export const selectLooping = () => createSelector(
  selectPageState(),
  (state) => state.get('isLooping')
)
