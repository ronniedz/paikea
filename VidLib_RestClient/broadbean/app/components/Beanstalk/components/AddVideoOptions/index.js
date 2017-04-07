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
    this.state = {
      modal: true,
    }
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
    const { videoobj, videoitems, onAddToPlaylist } = this.props
    onAddToPlaylist(videoobj ? [videoobj] : videoitems, playlistids)
    this.closeChildDialog()
  }

  closeChildDialog() {
    this.setState({ modal: false })
  }

  renderChildrenPlaylists(childrendata) {
    return childrendata.map((child, index) => (
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

  renderVideoInfo(videoobj) {
    return (
      videoobj ?
      (
        <div>
          <div><b>{videoobj.title}</b></div>
          <img role="presentation" src={videoobj.defaultThumbnail} />
        </div>
      )
      : null
    )
  }

  renderVideoItemTitles(videos) {
    return (
      videos ?
      (
        <ul>
          {
            videos.map((ea) => (<li key={ea.videoId}>{ea.title}</li>))
          }
        </ul>
      )
      : null
    )
  }

  render() {
    const { videoobj, videoitems, userchildren } = this.props
    const { modal } = this.state
    return (
      <BeanModal isOpen={modal} height="650px">
        <button onClick={this.closeChildDialog}>close</button>
        {this.renderVideoInfo(videoobj)}
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
  videoobj: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]).isRequired,
  videoitems: PropTypes.array,
}

export default AddVideoOptions
