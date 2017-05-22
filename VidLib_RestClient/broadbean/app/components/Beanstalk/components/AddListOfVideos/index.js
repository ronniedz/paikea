/**
* @Author: gajo
* @Date:   2016-07-19T09:50:11-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-13T23:26:44-07:00
*/

/**
*
* AddListOfVideos
*
*/

import React, { PropTypes, Component } from 'react'
import styles from './styles.css'
import AddVideoOptions from '../AddVideoOptions'

class AddListOfVideos extends Component {
  constructor(props) {
    super(props)

    this.state = {
      renderOptions: false,
      optionsVisible: true,
    }
    this.toggleOffVisible = this.toggleOffVisible.bind(this)
  }

  openAddVideoDialog(videos) {
    if (videos && videos.length > 0) {
      this.setState({ renderOptions: true, optionsVisible: true })
    }
  }

  toggleOffVisible() {
    this.setState({ optionsVisible: false })
  }

  renderAddOptions(videoitems, userchildren, onAddToPlaylist) {
    return this.state.renderOptions
    ? (
      <AddVideoOptions
        videoitems={videoitems}
        isVisible={this.state.optionsVisible}
        toggleOffVisible={this.toggleOffVisible}
        {...{ userchildren, onAddToPlaylist }}
      />
    )
    : null
  }

  render() {
    const { playlists, listindex, userchildren, onAddToPlaylist } = this.props
    return (
      <div className={styles.addListOfVideos}>
        <button
          onClick={() => {
            this.openAddVideoDialog(playlists[listindex].videos)
          }}
        >
          add all videos
        </button>
        {this.renderAddOptions(playlists[listindex].videos, userchildren, onAddToPlaylist)}
      </div>
    )
  }
}

AddListOfVideos.propTypes = {
  listindex: PropTypes.number,
  playlists: PropTypes.array,
  userchildren: PropTypes.object,
  onAddToPlaylist: PropTypes.func,
}

export default AddListOfVideos
