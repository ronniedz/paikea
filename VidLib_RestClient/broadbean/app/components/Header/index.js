/**
* @Author: gajo
* @Date:   2016-06-15T00:04:22-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-09T00:56:08-08:00
*/

/**
*
* Header
*
*/

/* eslint-disable no-console */

import React from 'react'
import beancab from './beancab.png'
import styles from './styles.css'
import { push } from 'react-router-redux'
import { connect } from 'react-redux'
import LocaleToggle from 'containers/LocaleToggle'
import GoogleSignIn from 'components/GoogleSignIn'
import { gapicid } from 'siteconfig'

export function Header(props) {
  const { changeRoute, setAuthorized } = props

  const responseGoogle = (response) => {

  }

  return (
    <div className={styles.header}>
      <div className={styles.container}>
        <img src={beancab} alt="mini logo" />
        <span>
          <a
            className={styles.brandTxt}
            href="#"
            onClick={() => changeRoute('/')}
          >
            bean.cab
          </a>
        </span>
        <div className={styles.googlogin}>
          <div className={styles.headeratomwrap}>
            <GoogleSignIn
              clientId={gapicid}
              callback={responseGoogle}
              offline={false}
              setAuthorized={setAuthorized}
            />
            <LocaleToggle />
          </div>
        </div>
      </div>
    </div>
  )
}

Header.propTypes = {
  changeRoute: React.PropTypes.func,
  setAuthorized: React.PropTypes.func,
}

// react-redux stuff
function mapDispatchToProps(dispatch) {
  return {
    changeRoute: (url) => dispatch(push(url)),
    dispatch,
  }
}

export default connect(null, mapDispatchToProps)(Header)
