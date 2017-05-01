import expect from 'expect'
import staticPagesReducer from '../reducer'
import { fromJS } from 'immutable'
import { requestHTML, requestHTMLLoaded } from '../actions'

describe('staticPagesReducer', () => {
  let state
  beforeEach(() => {
    state = fromJS({
      pathname: '',
      pagecontent: 'static page content',
    })
  })

  it('returns the initial state', () => {
    const expectedResult = state
    expect(staticPagesReducer(undefined, {})).toEqual(fromJS(expectedResult))
  })

  it('should set pathname actions correctly', () => {
    const path = 'foo'
    const expectedResult = state
        .set('pathname', path)
    expect(staticPagesReducer(state, requestHTML(path))).toEqual(fromJS(expectedResult))
  })

  it('should set pagecontent actions correctly', () => {
    const content = 'foo'
    const expectedResult = state
        .set('pagecontent', content)
    expect(staticPagesReducer(state, requestHTMLLoaded(content))).toEqual(fromJS(expectedResult))
  })
})
