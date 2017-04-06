/**
* @Author: gajo
* @Date:   2016-12-08T10:45:14-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T21:59:28-08:00
*/

/* eslint-disable no-unused-vars */

/*
 *
 * OffspringPage
 *
 */

import React, { PropTypes, Component } from 'react'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'
import {
  selectCurrentIndexes,
  selectPlaylists,
  selectVideoObj,
  selectLooping,
} from './selectors'
import { push } from 'react-router-redux'
import auth from 'utils/auth'
import Modal from 'react-modal'
import { List } from 'immutable'
import {
  changeVideo,
  createPlaylist,
  deleteFromPlaylist,
  retrievePlaylists,
  toggleLooping,
} from './actions'
import Beanstalk from 'components/Beanstalk'
import { dispatches, stateprops } from '../shared'
import BeanModal from 'components/BeanModal'
import { FormattedMessage } from 'react-intl'
import messages from './messages'

// eslint-disable-line react/prefer-stateless-function
class OffspringPage extends Component {
  constructor(props) {
    super(props)
    this.state = {
      modal: false,
    }
    this.openNewPlaylistDialog = this.openNewPlaylistDialog.bind(this)
    this.closeNewPlaylistDialog = this.closeNewPlaylistDialog.bind(this)
    this.createNewPlaylist = this.createNewPlaylist.bind(this)
  }

  componentDidMount() {
    if (auth.loggedIn()) {
      this.props.dispatch(retrievePlaylists(this.props.params.id))
    }
  }

  componentWillReceiveProps(nextprops) {
    const paramid = this.props.params.id
    const nextid = nextprops.params.id
    if (paramid !== nextid) {
      this.props.dispatch(retrievePlaylists(nextid))
    }
  }

  openNewPlaylistDialog() {
    this.setState({
      modal: true,
      modalmsg: '',
    })
  }

  closeNewPlaylistDialog() {
    this.setState({
      modal: false,
    })
  }

  createNewPlaylist(evt) {
    evt.preventDefault()
    const title = evt.currentTarget.querySelector('#playlisttitle').value.trim()
    const { params } = this.props
    if (title !== '') {
      this.props.dispatch(createPlaylist(title, params.id))
      this.setState({ modal: false })
    } else {
      this.setState({ modalmsg: 'Please fill in a title' })
    }
  }

  render() {
    const {
      userplaylists,
      ...others,
    } = this.props
    const { modal, modalmsg } = this.state
    return (
      <div>
        <Beanstalk
          playlists={userplaylists.toJS()}
          {...others}
        >
          <button onClick={this.openNewPlaylistDialog}>
            <FormattedMessage {...messages.createPlaylist} />
          </button>
        </Beanstalk>
        <BeanModal isOpen={modal} height="170px">
          <button onClick={this.closeNewPlaylistDialog}>close</button>
          <form onSubmit={this.createNewPlaylist} >
            <label>Title:</label>
            <input style={{ border: 'black .01em solid' }} id="playlisttitle" type="text" />
            <br />
            <input type="submit" />
            <fieldset>{modalmsg}</fieldset>
          </form>
        </BeanModal>
      </div>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  currentindexes: selectCurrentIndexes(),
  userplaylists: selectPlaylists(),
  videoobj: selectVideoObj(),
  isLooping: selectLooping(),
  ...stateprops,
})

function mapDispatchToProps(dispatch) {
  return {
    onChangeVideo: (data, offspringId) => {
      dispatch(changeVideo(data, offspringId))
    },
    onDeleteFromPlaylist: (playlistid, videoid) => {
      dispatch(deleteFromPlaylist(playlistid, videoid))
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

OffspringPage.defaultProps = {
  enabledeletevideo: true,
}

OffspringPage.propTypes = {
  dispatch: PropTypes.func,
  userplaylists: PropTypes.instanceOf(List),
  params: PropTypes.object,
  videoobj: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
}

export default connect(mapStateToProps, mapDispatchToProps)(OffspringPage)
