/**
* @Author: gajo
* @Date:   2016-06-19T21:41:10-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-20T00:37:24-07:00
*/

import { expect } from 'chai'
import { mount } from 'enzyme'
import React from 'react'
import Footer from '../index'
import { IntlProvider } from 'react-intl'

describe('<Footer />', () => {
  it('should render Privacy link', () => {
    const renderedComponent = mount(
      <IntlProvider locale="en">
        <Footer />
      </IntlProvider>
    )
    expect(renderedComponent.find({ href: '/privacy' })).to.have.length(1)
  })

  it('should render About link', () => {
    const renderedComponent = mount(
      <IntlProvider locale="en">
        <Footer />
      </IntlProvider>
    )
    expect(renderedComponent.find({ href: '/about' })).to.have.length(1)
  })

  it('should render Contact link', () => {
    const renderedComponent = mount(
      <IntlProvider locale="en">
        <Footer />
      </IntlProvider>
    )
    expect(renderedComponent.find({ href: '/contact' })).to.have.length(1)
  })
})
