/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-17T20:52:52-07:00
*/

/**
 * Testing our Button component
 */

import Button from '../index'

import expect from 'expect'
import { shallow } from 'enzyme'
import React from 'react'

describe('<Button />', () => {
  it('should render its children', () => {
    const children = (<h1>Test</h1>)
    const renderedComponent = shallow(
      <Button href="http://example.com">
        {children}
      </Button>
    )
    expect(renderedComponent.contains(children)).toEqual(true)
  })

  it('should adopt the className', () => {
    const renderedComponent = shallow(<Button className="test" >test</Button>)
    expect(renderedComponent.find('a').hasClass('test')).toEqual(true)
  })

  it('should render an <a> tag if no route is specified', () => {
    const renderedComponent = shallow(<Button href="http://example.com" >test</Button>)
    expect(renderedComponent.find('a').length).toEqual(1)
  })

  it('should render a button to change route if the handleRoute prop is specified', () => {
    const renderedComponent = shallow(<Button handleRoute={function handler() {}} >test</Button>)

    expect(renderedComponent.find('button').length).toEqual(1)
  })

  it('should handle click events', () => {
    const onClickSpy = expect.createSpy()
    const renderedComponent = shallow(<Button onClick={onClickSpy} >test</Button>)
    renderedComponent.find('a').simulate('click')
    expect(onClickSpy).toHaveBeenCalled()
  })
})
