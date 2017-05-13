/**
* @Author: gajo
* @Date:   2016-06-13T21:32:59-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T01:10:55-08:00
*/

/**
 *
 * App.react.js
 *
 * This component is the skeleton around the actual pages, and should only
 * contain code that should be seen on all pages. (e.g. navigation bar)
 */

/* eslint-disable no-undef */

import React, { PropTypes } from 'react'

// Import the CSS reset, which HtmlWebpackPlugin transfers to the build folder
import 'sanitize.css/sanitize.css'

import Header from 'components/Header'

import styles from './styles.css'
import backdrop from './ample-colorful-storage-in-kids-bedroom.jpg'

import {
  connect,
} from 'react-redux'
import {
  createStructuredSelector,
} from 'reselect'
import {
  selectAgeGroup,
  selectAuthorizedBy,
  selectVideoDimensions,
  selectVideoMode,
} from './selectors'
import {
  setAuthorizedBy,
  setVidDimensions,
} from './actions'
import {
  videoassets as vidconfig,
} from 'siteconfig'
import {
  find,
  curry,
  throttle,
  once,
} from 'lodash'
import R from 'ramda'
import { routes } from 'clientpaths.json'

class App extends React.Component {
  componentDidMount() {
    const { setVidDim, location } = this.props
    // remap the horizontal thresholds for window resize
    this.hthresholds = new Map(vidconfig.dimensions.map((ea) => [ea.upperthreshold, ea]))

    const staticRoutes = R.pickBy((v, k) => v.hasOwnProperty('file'))(routes)
    const staticpaths = R.values(R.map(e => e.path)(staticRoutes))

    if (!R.keys(staticRoutes).includes(location.pathname)) {
      this.resizeApp = this.resizeApp.bind(this)
      this.resizeVideoScrollingUp = this.resizeVideoScrollingUp.bind(this)
      window.onresize = throttle(this.resizeApp, 250)
      document.addEventListener('scroll', throttle(this.resizeVideoScrollingUp, 250))
    }

    const defaultdim = find(vidconfig.dimensions, (ea) => ea.upperthreshold > window.innerWidth)

    setVidDim.call(null, defaultdim)
  }

  resizeVideoScrollingUp() {
    const { viddim, setVidDim, videomode, setVideoMode } = this.props

    if (window.pageYOffset > 40 && videomode == 'full') {
      setVideoMode('topright')
    } else if (window.pageYOffset < 40 && videomode !== 'full') {
      setVideoMode('full')
    }
  }

  resizeApp() {
    const { viddim, setVidDim } = this.props
    const keys = this.hthresholds.keys()

    let iter = keys.next()
    while (iter.value < window.innerWidth || iter.done) {
      iter = keys.next()
    }

    if (viddim.width !== this.hthresholds.get(iter.value).width) {
      setVidDim.call(null, this.hthresholds.get(iter.value))
    }
  }

  render() {
    const { children, viddim, setAuthorized, location, videomode, ...others } = this.props
    return (
      <div>
        <div className={styles.mainwrap}>
          <div>
            <div
              className={styles.backdrop}
              style={{
                background: `url(${backdrop})`,
                backgroundRepeat: 'no-repeat',
                backgroundSize: 'cover',
              }}
            />
            <div className={styles.mainwrap}>
              <div
                className={styles.centercol}
                style={{ width: viddim.width }}
              >
                <Header {...others} setAuthorized={curry(setAuthorized)(_, location.pathname)} />
                {children}
              </div>
            </div>
          </div>
        </div>
      </div>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  viddim: selectVideoDimensions(),
  authby: selectAuthorizedBy(),
  agegroup: selectAgeGroup(),
  videomode: selectVideoMode(),
})

function mapDispatchToProps(dispatch) {
  return {
    setVidDim: (dim) => dispatch(setVidDimensions(dim)),
    setAuthorized: (authorized, path) => {
      dispatch(setAuthorizedBy(authorized, path))
    },
    setVideoMode: (mode) => dispatch(setVideoMode(mode)),
    dispatch,
  }
}

App.propTypes = {
  authby: PropTypes.oneOfType([PropTypes.object, PropTypes.bool]),
  children: PropTypes.node,
  dispatch: PropTypes.func,
  location: PropTypes.object,
  setAuthorized: PropTypes.func,
  setVidDim: PropTypes.func,
  setVideoMode: PropTypes.func,
  viddim: PropTypes.object,
  videomode: PropTypes.string,
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
