/**
* @Author: gajo
* @Date:   2016-07-19T09:50:11-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:23:40-08:00
*/

import AddListOfVideos from '../index'
import expect from 'expect'
import { mount } from 'enzyme'
import React from 'react'
import configureMockStore from 'redux-mock-store'
// import { ADD_VIDEO } from 'containers/App/constants'

const mockStore = configureMockStore()

describe('<AddListOfVideos />', () => {
  const store = mockStore({})
  const listindex = 0
  const fixture = [{
    videoId: '9H5RSKK4x9I',
    title: 'I\'ve Been Working On The',
    thumbnail: 'https://i.ytimg.com/vi/9H5RSKK4x9I/default.jp',
  }, {
    videoId: '9iMGFqMmUFs',
    title: 'What would happen if you',
    thumbnail: 'https://i.ytimg.com/vi/9iMGFqMmUFs/default.jp',
  }]

  const props = {
    playlists: fixture,
    listindex,
    store,
  }

  const renderedComponent = mount(<AddListOfVideos {...props} />)

  const button = renderedComponent.find('button')
  expect(button.length).toEqual(1)
  expect(button.text()).toEqual('add all videos')

  // button.simulate('click')
  // expect(store.getActions()[0].type).toEqual(ADD_VIDEO)
})
