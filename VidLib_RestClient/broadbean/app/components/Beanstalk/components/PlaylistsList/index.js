/**
* @Author: gajo
* @Date:   2016-06-24T09:28:26-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-14T23:18:15-07:00
*/

/**
*
* PlaylistsList
*
*/

import React, { PropTypes } from 'react'
import styles from './styles.css'
import PlaylistItem from '../PlaylistItem'
import AddListOfVideos from '../AddListOfVideos'
import { injectIntl } from 'react-intl'
import messages from './messages'
import Footer from 'components/Footer'

function playlistStack(props) {
  const { playlists, ...others } = props
  let bars = []
  for (let i = 0; i < playlists.length; i++) {
    const { prevpagetoken, nextpagetoken } = playlists[i]

    let prevpgtokenelem
    let nextpgtokenelem

    if (prevpagetoken) {
      prevpgtokenelem =
        (<a
          className={styles.pgtokenanchor}
          onClick={(evt) => props.onNavViaPageParam(evt.target.dataset)}
          data-listindex={i}
          data-pagetoken={prevpagetoken}
        >
          &lt
        </a>)
    }

    if (nextpagetoken) {
      nextpgtokenelem =
        (<a
          className={styles.pgtokenanchor}
          onClick={(evt) => props.onNavViaPageParam(evt.target.dataset)}
          data-listindex={i}
          data-pagetoken={nextpagetoken}
        >
          &gt
        </a>)
    }

    bars.push(
      <section
        key={i}
        className={styles.listgroup}
      >
        <PlaylistItem
          className={styles.playlistitem}
          listindex={i}
          playlists={playlists[i]}
          {...others}
        />
        {
          props.enableaddvideo && props.authby
          ? <AddListOfVideos listindex={i} playlists={playlists} {...others} />
          : null
        }
        <div className={styles.pagetokenwrap}>
          {prevpgtokenelem}
          {nextpgtokenelem}
        </div>
      </section>
    )
  }

  return (
    <div>
      {bars}
    </div>
  )
}

function PlaylistsList(props) {
  const { intl, videoobj } = props
  return (
    <div id="playlistparent" className={styles.playlistsList}>
      {videoobj &&
        <button onClick={() => props.onToggleLooping(!props.isLooping)}>
          {props.isLooping ? intl.messages[messages.loopVideos.stopLoop] : intl.messages[messages.loopVideos.doLoop]}
        </button>
      }
      {props.playlists && props.playlists.length > 0
        ? playlistStack(props)
        : null}
      <Footer />
    </div>
  )
}

PlaylistsList.propTypes = {
  playlists: PropTypes.array,
  isLooping: PropTypes.bool,
}

playlistStack.propTypes = {
  enableaddvideo: PropTypes.bool,
  authby: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  playlists: PropTypes.array,
  onNavViaPageParam: PropTypes.func,
  isLooping: PropTypes.bool,
}

export default injectIntl(PlaylistsList)
