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
} from 'lodash'

class App extends React.Component {
  componentDidMount() {
    const { setVidDim } = this.props

    // remap the horizontal thresholds for window resize
    this.hthresholds = new Map(vidconfig.dimensions.map((ea) => [ea.upperthreshold, ea]))

    this.resizeApp = this.resizeApp.bind(this)
    window.onresize = this.resizeApp

    const defaultdim = find(vidconfig.dimensions, (ea) => ea.upperthreshold > window.innerWidth)

    setVidDim.call(null, defaultdim)
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
    const { children, viddim, setAuthorized, location, ...others } = this.props
    console.log('--------------')
    console.log('this.props.location', this.props.location)
    console.log('this.props.location.pathname', this.props.location.pathname)
    console.log('this.props.location.state', this.props.location.state)
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
})

function mapDispatchToProps(dispatch) {
  return {
    setVidDim: (dim) => dispatch(setVidDimensions(dim)),
    setAuthorized: (authorized, path) => {
      dispatch(setAuthorizedBy(authorized, path))
    },
    dispatch,
  }
}

App.propTypes = {
  authby: PropTypes.oneOfType([PropTypes.object, PropTypes.bool]),
  children: PropTypes.node,
  dispatch: PropTypes.func,
  location: PropTypes.object,
  setVidDim: PropTypes.func,
  setAuthorized: PropTypes.func,
  viddim: PropTypes.object,
}

// export default App
export default connect(mapStateToProps, mapDispatchToProps)(App)
