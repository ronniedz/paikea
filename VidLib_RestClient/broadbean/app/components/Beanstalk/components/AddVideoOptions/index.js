/**
* @Author: gajo
* @Date:   2016-11-23T19:43:03-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-11-23T22:12:39-08:00
*/

/**
*
* AddVideoOptions
*
*/

import React, { Component, PropTypes } from 'react'
import BeanModal from 'components/BeanModal'

class AddVideoOptions extends Component {
  constructor(props) {
    super(props)
    this.renderChildPlaylist = this.renderChildPlaylist.bind(this)
    this.addVideo = this.addVideo.bind(this)
    this.closeChildDialog = this.closeChildDialog.bind(this)
  }

  addVideo(evt) {
    evt.preventDefault()
    const formEl = evt.currentTarget
    const inputEls = Array.from(formEl.querySelectorAll('input:checked').values())
    this.submitAddVideo(inputEls.map(ea => ea.value))
  }

  submitAddVideo(playlistids) {
    const { videoitems, onAddToPlaylist } = this.props
    onAddToPlaylist(videoitems, playlistids)
    this.closeChildDialog()
  }

  closeChildDialog() {
    this.props.toggleOffVisible.call()
  }

  renderChildrenPlaylists(userchildren) {
    return userchildren.map((child, index) => (
      <li key={index}>
        {child.name}
        <ul style={{ marginLeft: '8px' }}>
          {this.renderChildPlaylist(child.playlists)}
        </ul>
      </li>
    ))
  }

  renderChildPlaylist(playlists) {
    return playlists.map((playlist, index) => (
      <li key={index}>
        <label>{playlist.title}<input type="checkbox" value={playlist.id} /></label>
      </li>
    ))
  }

  renderVideoItemTitles(videoitems) {
    const thumbchild = (ea) => (
      <li key={ea.videoId}>
        <b>{ea.title}</b>
        <img role="presentation" src={ea.defaultThumbnail} />
      </li>
    )
    const titlechild = (ea) => (
      <li key={ea.videoId}>
        {ea.title}
      </li>
    )

    const litype = videoitems.length < 5 ? thumbchild : titlechild

    return (
      <ul>
        {videoitems.map(litype)}
      </ul>
    )
  }

  render() {
    const { videoitems, userchildren, isVisible } = this.props
    return (
      <BeanModal isOpen={isVisible} height="650px">
        <button onClick={this.closeChildDialog}>close</button>
        {this.renderVideoItemTitles(videoitems)}
        <div>
          <form onSubmit={this.addVideo}>
            <h5>Playlists:</h5>
            <ul>{this.renderChildrenPlaylists(userchildren)}</ul>
            <input type="submit" value="submit" />
          </form>
        </div>
      </BeanModal>
    )
  }
}

AddVideoOptions.propTypes = {
  onAddToPlaylist: PropTypes.func,
  userchildren: PropTypes.object,
  videoitems: PropTypes.array,
  isVisible: PropTypes.bool,
  toggleOffVisible: PropTypes.func,
}

export default AddVideoOptions
