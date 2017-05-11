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
    }
    this.renderAddVideo = this.renderAddVideo.bind(this)
  }

  openAddVideoDialog(vidobj) {
    if (vidobj) {
      this.setState({ renderOptions: true })
    }
  }

  renderAddOptions(videoobj, userchildren, others) {
    return this.state.renderOptions
    ? (
      <AddVideoOptions
        {...{ videoobj, userchildren }}
        {...others}
      />
    )
    : null
  }

  renderAddVideo(videoobj, userchildren, others) {
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
        {this.renderAddOptions(videoobj, userchildren, others)}
      </div>
    )
  }

  render() {
    const { playlists, itemindex, videoobj, userchildren, ...others } = this.props
    return (
      <div>
        {(videoobj || playlists) ? this.renderAddVideo(videoobj || playlists.videos[itemindex], userchildren, others) : null}
      </div>
    )
  }
}

AddVideo.propTypes = {
  videos: PropTypes.oneOfType([PropTypes.array, PropTypes.bool]),
  itemindex: PropTypes.number,
  playlists: PropTypes.object,
  userchildren: PropTypes.object,
  videoobj: PropTypes.oneOfType([PropTypes.object, PropTypes.bool]),
}


export default AddVideo
