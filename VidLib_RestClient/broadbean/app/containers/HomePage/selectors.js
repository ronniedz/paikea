/**
* @Author: gajo
* @Date:   2016-06-21T00:41:36-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-19T13:14:42-07:00
*/

import { createSelector } from 'reselect'

/**
 * Homepage selectors
 */
export const selectPageState = () => (state) => state.get('home')

export const selectBeanRecommendsResults = () => createSelector(
  selectPageState(),
  (state) => state.get('recommendations')
)

export const selectCurrentIndexes = () => createSelector(
  selectPageState(),
  (state) => state.get('currentIndexes').toObject()
)

export const selectVideoObj = () => createSelector(
  selectPageState(),
  (state) => state.get('videoobj')
)
export const selectLooping = () => createSelector(
  selectPageState(),
  (state) => state.get('isLooping')
)
