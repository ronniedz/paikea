import expect from 'expect'
import {
  requestHTML,
  requestHTMLLoaded,
} from '../actions'

import {
  LOAD_HTML,
  LOAD_HTML_SUCCESS,
} from '../constants'

describe('StaticPages actions', () => {
  describe('request html', () => {
    it('has a type of DEFAULT_ACTION', () => {
      const fixture = 'foo'
      const expected = {
        type: LOAD_HTML,
        pathname: fixture,
      }
      expect(requestHTML(fixture)).toEqual(expected)
    })
    it('set load success', () => {
      const fixture = '<h1>foo</h1>'
      const expected = {
        type: LOAD_HTML_SUCCESS,
        content: fixture,
      }
      expect(requestHTMLLoaded(fixture)).toEqual(expected)
    })
  })
})
