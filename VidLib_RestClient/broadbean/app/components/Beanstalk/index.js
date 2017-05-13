/**
* @Author: gajo
* @Date:   2016-06-14T21:03:28-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T21:54:43-08:00
*/

/**
*
* Beanstalk
*
*/

import React, { PropTypes } from 'react'

import VideoPlayback from './components/VideoPlayback'
import PlaylistsList from './components/PlaylistsList'
import NavMenu from './components/NavMenu'
import styles from './styles.css'

function Beanstalk(props) {
  const { children, currentindexes, viddim, videoobj, isLooping, onChangeVideo, videomode, ...others } = props
  return (
    <div>
      <div className={styles.navmenu} style={{ width: viddim.width }}>
        <section>
          <NavMenu {...others} />
        </section>
      </div>
      <div className={styles.marginparent}>
        <section className={styles.videouiparent}>
          <VideoPlayback
            autoplay={0}
            enableaddvideo={props.enableaddvideo}
            authby={props.authby}
            userchildren={props.userchildren}
            onAddToPlaylist={props.onAddToPlaylist}
            {...{
              videoobj,
              viddim,
              currentindexes,
              isLooping,
              onChangeVideo,
              videomode,
            }}
          />
        </section>
        <section
          style={{ top: videoobj ? viddim.height + 80 : 54 }}
          className={styles.playlistwrapper}
        >
          {children}
          <PlaylistsList {...props} />
        </section>
      </div>
    </div>
  )
}

Beanstalk.propTypes = {
  authby: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  children: PropTypes.oneOfType([PropTypes.array, PropTypes.object]),
  currentindexes: PropTypes.object,
  enableaddvideo: PropTypes.bool,
  isLooping: PropTypes.bool,
  onAddToPlaylist: PropTypes.func,
  onChangeVideo: PropTypes.func,
  playlists: PropTypes.array,
  userchildren: PropTypes.object,
  viddim: PropTypes.object,
  videoobj: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  videomode: PropTypes.string,
}

// export default Beanstalk
export default Beanstalk
