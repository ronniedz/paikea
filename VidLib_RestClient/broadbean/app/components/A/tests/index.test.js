/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-17T20:49:29-07:00
*/

/**
 * Testing our link component
 */

import A from '../index'

import expect from 'expect'
import { shallow } from 'enzyme'
import React from 'react'

describe('<A />', () => {
  it('should render its children', () => {
    const children = (<h1>Test</h1>)
    const renderedComponent = shallow(
      <A href="/">
        {children}
      </A>
    )
    expect(renderedComponent.contains(children)).toEqual(true)
  })

  it('should adopt the className', () => {
    const renderedComponent = shallow(<A href="/" className="test" >test</A>)
    expect(renderedComponent.find('a').hasClass('test')).toEqual(true)
  })

  it('should adopt the href', () => {
    const renderedComponent = shallow(<A href="example.com" >test</A>)
    expect(renderedComponent.prop('href')).toEqual('example.com')
  })

  it('should adopt the target', () => {
    const renderedComponent = shallow(<A href="/" target="_blank" >test</A>)
    expect(renderedComponent.prop('target')).toEqual('_blank')
  })
})
