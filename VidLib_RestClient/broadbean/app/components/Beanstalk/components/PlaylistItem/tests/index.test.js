/**
 * @Author: gajo
 * @Date:   2016-06-24T21:51:44-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-10-31T00:33:10-07:00
 */

import PlaylistItem from '../index'

import expect from 'expect'
import {
  mount,
} from 'enzyme'
import React from 'react'

describe('<PlaylistItem />', () => {
  it('should render children elements', () => {
    const fixture = {
      title: 'Reading',
      description: 'Lorem Ipsum blah bleh',
      videos: [{
        videoId: 'SAkp7oOTwK8',
        title: '3 Weird Things That Dome',
        defaultThumbnail: 'https://i.ytimg.com/vi/SAkp7oOTwK8/default.jpg',
      }],
    }
    const currentindexes = {
      playlists: 0,
      videos: 0,
    }
    const viddim = {
      thumbwidth: 0,
    }

    const spy = expect.createSpy()

    const pli =
    (
      <PlaylistItem
        viddim={viddim}
        currentindexes={currentindexes}
        listindex={0}
        playlists={fixture}
        onChangeVideo={spy}
        params={{ id: 'foo' }}
      />
    )

    const renderedComponent = mount(pli)
    expect(renderedComponent.find('.topbar').text()).toInclude(fixture.title)
    expect(renderedComponent.find('.topbar').text()).toInclude(fixture.description)

    const a = renderedComponent.find('a')

    expect(a.length).toEqual(1)
    expect(a.prop('href')).toEqual(`#${fixture.videos[0].videoId}`)
    expect(a.prop('data-playlists')).toEqual(currentindexes.playlists)
    expect(a.prop('data-videos')).toEqual(currentindexes.videos)
    a.simulate('click')
    expect(spy).toHaveBeenCalled()

    expect(renderedComponent.find('img').length).toEqual(1)
    expect(renderedComponent.find('img').prop('src')).toEqual(fixture.videos[0].defaultThumbnail)
    expect(renderedComponent.find('img').prop('title')).toEqual(fixture.videos[0].title)

    expect(renderedComponent.find('.listgroupswrap').length).toEqual(1)
    expect(renderedComponent.find('.listgroupswrap').text()).toInclude(fixture.videos[0].title)
  })
})
