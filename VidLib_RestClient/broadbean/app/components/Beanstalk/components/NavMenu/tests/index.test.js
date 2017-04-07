/**
* @Author: gajo
* @Date:   2016-06-21T16:53:00-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-11-11T01:32:03-08:00
*/

/* eslint-disable no-console */

import NavMenu from '../index'
import expect from 'expect'
import { mount } from 'enzyme'
import React from 'react'
import configureMockStore from 'redux-mock-store'
import { List } from 'immutable'
import { IntlProvider, defineMessages } from 'react-intl'

const mockStore = configureMockStore()

describe('<NavMenu />', () => {
  it('should contain an input search field and search submit button', () => {
    const store = mockStore({})
    const compprops = {
      onSearchSubmitForm: expect.createSpy(),
      onChangeMainSearch: expect.createSpy(),
      changeRoute: expect.createSpy(),
      submitval: 'testval',
      userchildren: List([]),
      authby: {},
      store,
    }

    // const defaultEnMessage = 'someContent'
    // const defaultDeMessage = 'someOtherContent'
    const messages = defineMessages({
      en: {
        id: 'app.components.LocaleToggle.en',
        defaultMessage: '',
      },
      // de: {
      //   id: 'app.components.LocaleToggle.de',
      //   defaultMessage: defaultDeMessage,
      // },
    })

    const renderedComponent = mount(
      <IntlProvider locale="en">
        <NavMenu {...compprops} messages={messages} />
      </IntlProvider>
    )

    const recanchor = renderedComponent.find('a')
    expect(recanchor.length).toEqual(1)

    expect(recanchor.at(0).text()).toEqual('Recommendations')
    recanchor.at(0).simulate('click')
    expect(compprops.changeRoute).toHaveBeenCalled()

    const inputs = renderedComponent.find('input')
    expect(inputs.at(0).get(0).value).toEqual(compprops.submitval)
    inputs.at(0).simulate('change', { target: { value: 'new value' } })
    expect(compprops.onChangeMainSearch).toHaveBeenCalled()
    // expect(inputs.at(1).get(0).type).toEqual('submit')

    renderedComponent.find('form#searchsubmit').simulate('submit')
    expect(compprops.onSearchSubmitForm).toHaveBeenCalled()

    // test these elements too
      // navchild
      // createchild
  })
})
