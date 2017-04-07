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
    }
  }

  openAddVideoDialog(videos) {
    if (videos && videos.length > 0) {
      this.setState({ renderOptions: true })
    }
  }

  renderAddOptions(videoitems, userchildren, others) {
    return this.state.renderOptions
    ? (
      <AddVideoOptions
        {...{ videoitems, userchildren }}
        {...others}
      />
    )
    : null
  }

  render() {
    const { playlists, listindex, userchildren, ...others } = this.props
    return (
      <div className={styles.addListOfVideos}>
        <button
          onClick={() => {
            this.openAddVideoDialog(playlists[listindex].videos)
          }}
        >
          ++
        </button>
        {this.renderAddOptions(playlists[listindex].videos, userchildren, others)}
      </div>
    )
  }
}

AddListOfVideos.propTypes = {
  listindex: PropTypes.number,
  playlists: PropTypes.array,
  userchildren: PropTypes.object,
}

export default AddListOfVideos
