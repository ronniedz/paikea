/**
 * @Author: gajo
 * @Date:   2016-07-19T09:49:16-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:23:12-08:00
 */

import AddVideo from '../index'
import expect from 'expect'
import { mount } from 'enzyme'
import React from 'react'
import configureMockStore from 'redux-mock-store'
import { List } from 'immutable'
// import { ADD_VIDEO } from 'containers/App/constants'

const mockStore = configureMockStore()

describe('<AddVideo />', () => {
  const store = mockStore({})

  const fixture = {
    videoId: '-elOa0ZvoeY',
    title: 'Letter R- Olive and The ',
    thumbnail: 'https://i.ytimg.com/vi/-elOa0ZvoeY/default.jpg',
  }
  const fixture2 = {
    userchildren: List([{
      id: 1,
      playlists: [],
      name: 'Freddie Mac',
      ageGroupId: 7,
      userId: 1,
    }]),
  }
  const renderedComponent = mount(
    <AddVideo
      videoobj={fixture}
      {...fixture2}
      store={store}
    />
  )

  const button = renderedComponent.find('button')
  expect(button.length).toEqual(1)
  expect(button.text()).toEqual('add me')

  // button.simulate('click')
  // expect(store.getActions()[0].type).toEqual(ADD_VIDEO)
})
