/**
* @Author: gajo
* @Date:   2016-06-29T01:29:27-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-19T16:11:03-07:00
*/

import { createSelector } from 'reselect'

export const selectPageState = () => state => state.get('search')

export const selectYTBeanResults = () => createSelector(
  selectPageState(),
  (state) => state.get('ytSearch')
)

export const selectSearchHistory = () => createSelector(
  selectPageState(),
  (state) => state.get('searchHistory')
)

export const selectLoadCompleteStatus = () => createSelector(
  selectPageState(),
  (state) => state.get('loadComplete')
)

export const selectNavToken = () => createSelector(
  selectPageState(),
  (state) => state.get('navToken')
)

export const selectCurrentIndexes = () => createSelector(
  selectPageState(),
  (state) => state.get('currentIndexes').toObject()
)

export const selectAddVideo = () => createSelector(
  selectPageState(),
  (state) => (
    {
      videoobj: state.getIn(['associateVideo', 'videoobj']),
      playlists: state.getIn(['associateVideo', 'options', 'playlists']),
    }
  )
)

export const selectVideoObj = () => createSelector(
  selectPageState(),
  (state) => state.get('videoobj')
)

export const selectLooping = () => createSelector(
  selectPageState(),
  (state) => state.get('isLooping')
)
