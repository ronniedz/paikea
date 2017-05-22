/**
 * @Author: gajo
 * @Date:   2016-11-23T19:45:11-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-11-23T21:29:52-08:00
 */

import AddVideoOptions from '../index'
import expect from 'expect'
import { mount } from 'enzyme'
import React from 'react'
import configureMockStore from 'redux-mock-store'
import { List } from 'immutable'

const mockStore = configureMockStore()

describe('<AddVideoOptions />', () => {
  const store = mockStore()
  const fixture2 = {
    videoId: '-elOa0ZvoeY',
    title: 'Letter R- Olive and The ',
    thumbnail: 'https://i.ytimg.com/vi/-elOa0ZvoeY/default.jpg',
  }
  const fixture = {
    userchildren: List([{
      id: 1,
      playlists: [],
      name: 'Freddie Kruger',
      ageGroupId: 7,
      userId: 1,
    }]),
  }
  const renderedComponent = mount(
    <AddVideoOptions
      videoitems={[fixture2]}
      {...fixture}
      store={store}
    />
  )

  const modal = renderedComponent.find('BeanModal')
  expect(modal.length).toEqual(1)
})
