/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-11-11T01:07:50-08:00
*/

/*
 * HomePage
 *
 * This is the first thing users see of our App, at the '/' route
 */

import React, { PropTypes } from 'react'
import { connect } from 'react-redux'
import { push } from 'react-router-redux'
import { createStructuredSelector } from 'reselect'
import { List } from 'immutable'
import {
  selectCurrentIndexes,
  selectBeanRecommendsResults,
  selectVideoObj,
  selectLooping,
} from './selectors'

import {
  retrieveRecommendations,
  changeVideo,
  toggleLooping,
} from './actions'

// bean
import Beanstalk from 'components/Beanstalk'

import { dispatches, stateprops } from '../shared'

export class HomePage extends React.Component {
  componentDidMount() {
    this.props.dispatch(retrieveRecommendations())
  }

  render() {
    const {
      recommendations,
      ...others,
    } = this.props

    return (
      <div>
        <Beanstalk
          playlists={recommendations.toJS()}
          {...others}
        />
      </div>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  currentindexes: selectCurrentIndexes(),
  recommendations: selectBeanRecommendsResults(), // should be refactored to generic 'list'
  videoobj: selectVideoObj(),
  isLooping: selectLooping(),
  ...stateprops,
})

function mapDispatchToProps(dispatch) {
  return {
    onChangeVideo: (data) => {
      dispatch(changeVideo(data))
    },
    onSearchSubmitForm: (evt) => {
      if (evt !== undefined && evt.preventDefault) evt.preventDefault()
      if (evt.target.querySelector('#searchinput').value !== '') dispatch(push('/search'))
    },
    onToggleLooping: (value) => {
      dispatch(toggleLooping(value))
    },
    ...dispatches(dispatch),
    dispatch,
  }
}

HomePage.propTypes = {
  dispatch: PropTypes.func,
  recommendations: PropTypes.instanceOf(List),
}

HomePage.defaultProps = {
  enableaddvideo: true,
}

// Wrap the component to inject dispatch and state into it
export default connect(mapStateToProps, mapDispatchToProps)(HomePage)
