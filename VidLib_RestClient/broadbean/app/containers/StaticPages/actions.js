/*
 *
 * StaticPages actions
 *
 */

import {
  LOAD_HTML,
  LOAD_HTML_SUCCESS,
} from './constants'

export function requestHTML(pathname) {
  return {
    type: LOAD_HTML,
    pathname,
  }
}

export function requestHTMLLoaded(content) {
  return {
    type: LOAD_HTML_SUCCESS,
    content,
  }
}
