/**
* @Author: gajo
* @Date:   2016-06-28T21:39:08-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-11-11T01:08:41-08:00
*/

/*
 *
 * SearchPage
 *
 */

/* eslint-disable no-alert */

import React, { PropTypes } from 'react'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'
import { List } from 'immutable'
import { search } from 'siteconfig'

import {
  selectCurrentIndexes,
  selectYTBeanResults,
  selectSearchHistory,
  selectLoadCompleteStatus,
  selectVideoObj,
  selectLooping,
} from './selectors'

import {
  addToPlaylist,
  loadYTSearch,
  changeVideo,
  toggleLooping,
} from './actions'

import Beanstalk from 'components/Beanstalk'

import { dispatches, stateprops } from '../shared'

export class SearchPage extends React.Component { // eslint-disable-line react/prefer-stateless-function
  componentDidMount() {
    const { searchval } = this.props

    if (searchval && searchval.trim().length > 0) {
      this.props.dispatch(loadYTSearch())
    }
  }

  render() {
    const {
      searchhistory,
      loadcompleted,
      searchval,
      ytsearch,
      ...others,
    } = this.props

    // this code logic is in a high level component for now and is expected to migrate further down as design is further defined
    if (searchval.trim() !== '' && !ytsearch && loadcompleted) {
      alert('no records found')
    }
    return (
      <div>
        <Beanstalk
          playlists={searchhistory.toJS()}
          {...others}
        />
      </div>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  loadcompleted: selectLoadCompleteStatus(),
  currentindexes: selectCurrentIndexes(),
  searchhistory: selectSearchHistory(),
  ytsearch: selectYTBeanResults(),
  videoobj: selectVideoObj(),
  isLooping: selectLooping(),
  ...stateprops,
})

function mapDispatchToProps(dispatch) {
  return {
    onChangeVideo: (data) => {
      dispatch(changeVideo(data))
    },
    onNavViaPageParam: (data) => {
      dispatch(loadYTSearch(data))
    },
    onSearchSubmitForm: (evt) => {
      if (evt !== undefined && evt.preventDefault) evt.preventDefault()
      if (evt.target.querySelector('#searchinput').value !== '') dispatch(loadYTSearch())
    },
    onAddToPlaylist: (vidobj, playlistids) => {
      dispatch(addToPlaylist(vidobj, playlistids))
    },
    onToggleLooping: (value) => {
      dispatch(toggleLooping(value))
    },
    dispatch,
    ...dispatches(dispatch),
  }
}

SearchPage.propTypes = {
  authby: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  dispatch: PropTypes.func,
  loadcompleted: PropTypes.bool,
  searchhistory: PropTypes.instanceOf(List),
  searchval: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
  titleprepend: PropTypes.string,
  ytsearch: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
}

SearchPage.defaultProps = {
  titleprepend: search.titleprepend,
  enableaddvideo: true,
}

export default connect(mapStateToProps, mapDispatchToProps)(SearchPage)
