/**
* @Author: gajo
* @Date:   2016-06-17T22:19:33-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-10-31T00:49:59-07:00
*/

import { Header } from '../index'
import { shallow } from 'enzyme'
import expect from 'expect'
import React from 'react'

describe('<Header />', () => {
  it('should contain the domain name-bean.cab', () => {
    const props = { authby: false }
    const reactelem = <Header {...props} />
    const renderedComponent = shallow(reactelem)
    expect(renderedComponent.find('button').first().text()).toEqual('bean.cab')
  })
})
