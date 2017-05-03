import { createSelector } from 'reselect'

/**
 * Direct selector to the staticPages state domain
 */
const selectPageState = () => state => state.get('staticPages')

/**
 * Other specific selectors
 */


/**
 * Default selector used by StaticPages
 */

const selectStaticPages = () => createSelector(
  selectPageState(),
  (state) => state.toJS()
)

export const selectPageContent = () => createSelector(
  selectPageState(),
  (state) => state.get('pagecontent')
)

export const selectPathname = () => createSelector(
  selectPageState(),
  (state) => state.get('pathname')
)

export default selectStaticPages
export {
  selectPageState,
}
