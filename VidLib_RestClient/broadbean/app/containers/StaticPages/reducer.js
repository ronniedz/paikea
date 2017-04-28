/*
 *
 * StaticPages reducer
 *
 */

import { fromJS } from 'immutable'
import {
  LOAD_HTML,
  LOAD_HTML_SUCCESS,
} from './constants'

const initialState = fromJS({
  pathname: '',
  pagecontent: 'static page content',
})

function staticPagesReducer(state = initialState, action) {
  switch (action.type) {
    case LOAD_HTML:
      return state.set('pathname', action.pathname)
    case LOAD_HTML_SUCCESS:
      return state.set('pagecontent', action.content)
    default:
      return state
  }
}

export default staticPagesReducer
