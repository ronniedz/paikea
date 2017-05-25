/**
* @Author: gajo
* @Date:   2016-06-24T21:51:44-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-15T00:11:18-07:00
*/

/* eslint-disable consistent-return */
/* eslint-disable new-cap */

/**
*
* PlaylistItem
*
*/

import React, { PropTypes } from 'react'
import styles from './styles.css'

import AddVideo from '../AddVideo'
import DeleteVideo from '../DeleteVideo'

function addVideo(videoobj, userchildren, onAddToPlaylist) {
  return (
    <AddVideo
      videoobj={videoobj}
      onAddToPlaylist={onAddToPlaylist}
      userchildren={userchildren}
    />
  )
}

function deleteVideo(videos, listindex, index, others) {
  return (
    <DeleteVideo
      itemindex={index}
      {...{ listindex, videos }}
      {...others}
    />
  )
}
/*
*/
function Thumbnails(videos, others) {
  const {
    onChangeVideo,
    listindex,
    currentindexes,
    authby,
    enableaddvideo,
    enabledeletevideo,
    viddim,
    playlists,
    onAddToPlaylist,
    userchildren,
    params,
  } = others
  return (
    <div className="listgroupswrap" >
      {videos.map((val, index) => (
        <div
          key={val.videoId}
          className={
            (currentindexes.playlists === listindex && currentindexes.videos === index)
            ? styles.borderitem
            : styles.thumbitem
          }
          style={{ width: `${viddim.thumbwidth}px` }}
        >
          <a
            href={`#${val.videoId}`}
            onClick={(evt) => {
              evt.preventDefault()
              onChangeVideo(evt.currentTarget.dataset, params.id)
            }}
            data-playlists={listindex}
            data-videos={index}
            data-videoobj={val}
          >
            <img
              title={val.title}
              alt="defaultThumbnail"
              src={val.defaultThumbnail}
            />
              {val.title.slice(0, 52)}...
          </a>
          <div className={styles.vidctrlwrap}>
            {authby && enableaddvideo &&
              addVideo(playlists.videos[index], userchildren, onAddToPlaylist)}
            {authby && enabledeletevideo &&
              deleteVideo(videos, listindex, index, others)}
          </div>
        </div>
        )
      )}
    </div>
  )
}

function PlaylistItem(props) {
  const { titleprepend, ...others } = props
  const { title, description, videos, message } = others.playlists
  return (
    <div className={styles.playlistparent}>
      <div className={`${styles.topbar} topbar`}>
        <div className={styles.leftbox_big}>
          {titleprepend}
          {title}
        </div>
        <div className={styles.rightbox}>
          {description}
        </div>
      </div>
      <div style={{ clear: 'both' }}>
        {message}
      </div>
      <div className={styles.thumbwrap} >
        {Thumbnails(videos, others)}
      </div>
    </div>
  )
}

PlaylistItem.propTypes = {
  currentvideoobj: React.PropTypes.object,
  listindex: React.PropTypes.number,
  onChangeVideo: PropTypes.func,
  playlists: PropTypes.shape({
    title: PropTypes.string,
    description: PropTypes.string,
    data: PropTypes.array,
  }),
  titleprepend: React.PropTypes.string,
  params: React.PropTypes.object,
}

export default PlaylistItem
