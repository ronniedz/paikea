/**
 * @Author: gajo
 * @Date:   2016-07-19T09:49:16-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:23:12-08:00
 */

import DeleteVideo from '../index'
import expect from 'expect'
import React from 'react'
import configureMockStore from 'redux-mock-store'
import { mountWithIntl } from 'utils/intlProvider-helper.js'

const delText = require('translations/en.json')['bean.components.DeleteVideo.delete']

const mockStore = configureMockStore()

describe('<DeleteVideo />', () => {
  it('should test children components', () => {
    const store = mockStore({})
    const spy = expect.createSpy()

    const fixture = {
      videoId: '-elOa0ZvoeY',
      title: 'Letter R- Olive and The ',
      thumbnail: 'https://i.ytimg.com/vi/-elOa0ZvoeY/default.jpg',
    }
    const fixture2 = {
      playlists: {
        id: 'plid',
        videos: [{ videoId: 'foo' }],
      },
      itemindex: 0,
      onDeleteFromPlaylist: spy,
    }

    const renderedComponent = mountWithIntl(
      <DeleteVideo
        videoobj={fixture}
        {...fixture2}
        store={store}
      />
    )

    expect(renderedComponent.text().trim()).toEqual('delete')
    renderedComponent.find('button').at(0).simulate('click', { preventDefault() {} })
    expect(spy).toHaveBeenCalledWith(fixture2.playlists.id, fixture2.playlists.videos[0].videoId)
    expect(renderedComponent.find('FormattedMessage').text()).toEqual(delText)
  })
})
