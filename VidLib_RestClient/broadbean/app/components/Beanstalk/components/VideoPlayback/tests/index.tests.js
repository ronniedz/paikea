/**
 * @Author: gajo
 * @Date:   2016-06-18T01:06:45-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-10-31T00:34:06-07:00
 */

import VideoPlayback from '../index'

import expect from 'expect'
import { shallow } from 'enzyme'
import React from 'react'

describe('<VideoPlayback />', () => {
  const fixture = {
    videoId: '-2pq6D5RhcM',
    title: 'Show Marvie Your Dance M',
    thumbnail: 'https://i.ytimg.com/vi/-2pq6D5RhcM/default.jpg',
  }

  const renderedComponent = shallow(<VideoPlayback videoobj={fixture} />)

  expect(renderedComponent.name()).toEqual('div')
  expect(renderedComponent.children().at(0).prop('id')).toEqual('player')
  // console.log('renderedComponent.debug()', renderedComponent.debug())
})
