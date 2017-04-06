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

import React, { PropTypes } from 'react'
import { FormattedMessage } from 'react-intl'
import messages from './messages'

function DeleteVideo(props) {
  return (
    <button
      onClick={
        (evt) => {
          evt.preventDefault()
          const { playlists, itemindex } = props
          const playlistid = playlists.id
          const videoid = playlists.videos[itemindex].videoId
          props.onDeleteFromPlaylist.call(null, playlistid, videoid)
        }
      }
    >
      <FormattedMessage {...messages.delete} />
    </button>
  )
}

DeleteVideo.propTypes = {
  onDeleteFromPlaylist: PropTypes.func,
}


export default DeleteVideo
