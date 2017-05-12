/**
* @Author: gajo
* @Date:   2016-04-24T18:51:28-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-16T22:33:20-07:00
*/

/**
 *  temp
*/

// http://stackoverflow.com/questions/11381673/detecting-a-mobile-browser

/*
window.mobilecheck = () => {
  const check = false
  (function(a) {if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))check = true})(navigator.userAgent||navigator.vendor||window.opera)
  return check
}

*/
import React, { Component, PropTypes } from 'react'
import styles from './styles.css'
import AddVideo from '../AddVideo'
import youtubeplayer from 'youtube-player'
import { isEqual, throttle } from 'lodash'

class VideoPlayback extends Component {
  constructor(props) {
    super(props)
    this.state = {
      config: {
        autoplay: props.autoplay || 1,
        controls: 1,
        fs: 1,
        showinfo: 0,
        autohide: 1,
        playsinline: 0,
      },
      videovisible: false,
      scrollYThreshold: 40,
      visibleAdd: true,
    }
    this.playerReady = this.playerReady.bind(this)
    this.onStateChange = this.onStateChange.bind(this)
    this.loopVideos = this.loopVideos.bind(this)
    this.resizeVideoScrollingUp = this.resizeVideoScrollingUp.bind(this)
  }

  componentDidMount() {
    this.player = youtubeplayer('player', this.state.config)
    this.player.on('ready', this.playerReady)
    this.player.on('stateChange', this.onStateChange)

    document.addEventListener('scroll', throttle(this.resizeVideoScrollingUp, 100))
  }

  componentWillReceiveProps(nextprops) {
    if (nextprops.videoobj) {
      this.setState({ videovisible: true })
    }
    this.checkPlayerSize(nextprops.viddim)
  }

  shouldComponentUpdate(nextprops) {
    return nextprops.videoobj !== null
  }

  async componentDidUpdate(prevprops) {
    const { videoobj, currentindexes } = this.props
    const style = document.querySelector('#player').style
    if (videoobj && !isEqual(currentindexes, prevprops.currentindexes)) {
      style.setProperty('display', 'block')
      try {
        await this.player.loadVideoById(videoobj.videoId)
      } catch (e) {
        console.log('error: ', e)
      }
    } else {
      const playerstate = await this.player.getPlayerState().then(s => s)
      if (!videoobj) {
        if (!playerstate || playerstate === 1) await this.player.pauseVideo()
        style.setProperty('display', 'none')
      }
    }
  }

  componentWillUnmount() {
    this.player.destroy()
  }

  onStateChange(evt) {
    if (evt.data === 0) {
      this.loopVideos()
    }
  }

  loopVideos() {
    const { isLooping, currentindexes } = this.props

    if (isLooping) {
      this.props.onChangeVideo(
        { ...currentindexes,
          videos:
          currentindexes.videos >= currentindexes.itemsLength - 1
          ? 0
          : currentindexes.videos + 1,
        }
      )
    }
  }

  playerReady() {
    this.ytiframe = document.querySelector('iframe')
    const { videoobj } = this.props

    // we cue the video for static landing on pages
    if (videoobj) {
      this.checkPlayerSize(this.props.viddim)
      this.player.cueVideoById(videoobj.videoId)
      this.setState({ videovisible: true })
    }
  }

  resizeVideoScrollingUp() {
    const { viddim } = this.props
    this.checkPlayerSize(viddim)
  }

  quarterView(vdim) {
    this.ytiframe.width = vdim.width / 2
    this.ytiframe.height = vdim.height / 2
    this.ytiframe.style.left = `${vdim.width / 2}px`
  }

  fullView(vdim) {
    this.ytiframe.width = vdim.width
    this.ytiframe.height = vdim.height
    this.ytiframe.style.left = '0px'
  }

  checkPlayerSize(vdim) {
    if (window.pageYOffset > this.state.scrollYThreshold) {
      this.quarterView(vdim)
      this.setState({ visibleAdd: false})
    } else if (this.ytiframe && vdim.width !== this.ytiframe.width) {
      this.fullView(vdim)
      this.setState({ visibleAdd: true})
    }
  }

  render() {
    const { authby, enableaddvideo, ...others } = this.props
    const { videovisible, visibleAdd } = this.state
    const visiblestyle = videovisible ? { display: 'block', visibility: 'visible' } : null

    return (
      <div
        className={styles.playerwrap}
        style={visiblestyle}
      >
        <div id="player" style={{ position: 'relative', pointerEvents: 'visible' }} />
        {authby && enableaddvideo && visibleAdd &&
          <div className={styles.addvidwrap}>
            <AddVideo {...others} />
          </div>
        }
      </div>
    )
  }
}

VideoPlayback.propTypes = {
  authby: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  autoplay: PropTypes.number,
  currentindexes: PropTypes.object,
  enableaddvideo: PropTypes.bool,
  videoid: PropTypes.string,
  videoobj: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  viddim: PropTypes.object,
  isLooping: PropTypes.bool,
  onChangeVideo: PropTypes.func,
}

export default VideoPlayback
