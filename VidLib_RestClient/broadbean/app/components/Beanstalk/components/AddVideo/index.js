/**
* @Author: gajo
* @Date:   2016-07-19T09:49:16-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:12:07-08:00
*/

/**
*
* AddVideo
*
*/

import React, { PropTypes, Component } from 'react'

import AddVideoOptions from '../AddVideoOptions'

import styles from './styles.css'

class AddVideo extends Component {
  constructor(props) {
    super(props)

    this.state = {
      renderOptions: false,
      optionsVisible: true,
    }
    this.renderAddVideo = this.renderAddVideo.bind(this)
    this.toggleOffVisible = this.toggleOffVisible.bind(this)
  }

  openAddVideoDialog(vidobj) {
    if (vidobj) {
      this.setState({ renderOptions: true, optionsVisible: true })
    }
  }

  toggleOffVisible() {
    this.setState({ optionsVisible: false })
  }

  renderAddVideo(videoobj, userchildren, onAddToPlaylist) {
    return (
      <div>
        <button
          className={styles.addVideoDialog}
          onClick={() => {
            this.openAddVideoDialog(videoobj)
          }}
        >
          add me
        </button>
        {this.state.renderOptions && // for
          <AddVideoOptions
            videoitems={[videoobj]}
            isVisible={this.state.optionsVisible}
            toggleOffVisible={this.toggleOffVisible}
            {...{ userchildren, onAddToPlaylist }}
          />
        }
      </div>
    )
  }

  render() {
    const { videoobj, userchildren, onAddToPlaylist } = this.props
    return (
      <div>
        {
          this.renderAddVideo(videoobj, userchildren, onAddToPlaylist)
        }
      </div>
    )
  }
}

AddVideo.propTypes = {
  // itemindex: PropTypes.number,
  // playlists: PropTypes.object,
  userchildren: PropTypes.object,
  videoobj: PropTypes.oneOfType([PropTypes.object, PropTypes.bool]),
  onAddToPlaylist: PropTypes.func,
}


export default AddVideo
