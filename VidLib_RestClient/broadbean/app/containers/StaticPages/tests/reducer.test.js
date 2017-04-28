import expect from 'expect'
import staticPagesReducer from '../reducer'
import { fromJS } from 'immutable'

describe('staticPagesReducer', () => {
  it('returns the initial state', () => {
    expect(staticPagesReducer(undefined, {})).toEqual(fromJS({
      pathname: '',
      pagecontent: 'static page content',
    }))
  })
})
