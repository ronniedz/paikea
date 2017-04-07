/**
* @Author: gajo
* @Date:   2016-08-19T02:25:53-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-19T10:54:54-07:00
*/

import React, { PropTypes, Component } from 'react'
import { FormattedMessage } from 'react-intl'
import messages from './messages'

class GoogleSignIn extends Component {
  constructor(props) {
    super(props)
    this.onLoginClick = this.onLoginClick.bind(this)
    this.onLogoutClick = this.onLogoutClick.bind(this)
    this.state = {
      auth2: {},
      signedIn: false,
    }
    this.loadGAPI = this.loadGAPI.bind(this)
  }

  componentDidMount() {
    this.embedGoogScript(document, 'script', 'google-login', this.loadGAPI)
  }

  onLoginClick() {
    const { auth2 } = this.state
    console.log('auth2', auth2)
    const { offline, redirectUri, callback } = this.props
    if (offline) {
      const options = {
        redirect_uri: redirectUri,
      }
      auth2.grantOfflineAccess(options)
        .then((data) => {
          callback(data)
        })
    } else {
      auth2.signIn()
        .then((response) => {
          console.log('response', response)
          callback(response)
        })
    }
  }

  onLogoutClick() {
    const { auth2 } = this.state
    auth2.signOut().then(() => {
      this.setState({ signedIn: false })
    })
  }

  embedGoogScript(d, s, id, cb) {
    const element = d.getElementsByTagName(s)[0]
    const fjs = element
    let js = element
    js = d.createElement(s)
    js.id = id
    js.src = '//apis.google.com/js/platform.js'
    fjs.parentNode.insertBefore(js, fjs)
    js.onload = cb
  }

  loadGAPI() {
    const {
      clientId,
      scope,
      cookiePolicy,
      loginHint,
      setAuthorized,
    } = this.props

    const params = {
      client_id: clientId,
      cookiepolicy: cookiePolicy,
      login_hint: loginHint,
      scope,
    }

    window.gapi.load('auth2', () => {
      window.gapi.auth2.init(params)
        .then(() => {
          const auth = window.gapi.auth2.getAuthInstance()
          const user = auth.currentUser.get()
          const loggedin = auth.isSignedIn.get()

          if (loggedin && !user.hasGrantedScopes(scope)) {
            auth.currentUser.get().grant({ scope })
          }

          console.log('!!!!!!!!!!!')
          
          console.log('user', user)
          console.log(user.getBasicProfile())
          console.log(user.getAuthResponse())

          if (loggedin) {
            setAuthorized({
              authorizer: 'google',
              profile: user.getBasicProfile(),
              id_token: user.getAuthResponse().id_token },
            )
          }

          auth.isSignedIn.listen((logstatus) => {
            if (logstatus) {
              setAuthorized({
                authorizer: 'google',
                profile: user.getBasicProfile(),
                id_token: user.getAuthResponse().id_token,
              })
            } else {
              setAuthorized(false)
            }

            this.setState({ signedIn: logstatus })
          })

          this.setState({
            auth2: auth,
            signedIn: loggedin,
          })
        })
    })
  }

  render() {
    const style = {
      display: 'inline-block',
      background: '#d14836',
      color: '#fff',
      width: 170,
      border: '1px solid transparent',
      fontSize: 11,
      fontWeight: 'bold',
      fontFamily: 'Roboto',
    }
    const { cssClass, children } = this.props
    const { signedIn } = this.state
    return (
      !signedIn
        ? <button
          className={cssClass}
          onClick={this.onLoginClick}
          style={cssClass ? {} : style}
        >
          {<FormattedMessage {...messages.login_google} />}
        </button>
        : <button
          className={cssClass}
          onClick={this.onLogoutClick}
          style={cssClass ? {} : style}
        >
          {<FormattedMessage {...messages.logout_google} />}
        </button>
    )
  }
}

GoogleSignIn.propTypes = {
  callback: PropTypes.func.isRequired,
  children: PropTypes.node,
  clientId: PropTypes.string.isRequired,
  cookiePolicy: PropTypes.string,
  cssClass: PropTypes.string,
  loginHint: PropTypes.string,
  loginText: PropTypes.string,
  logoutText: PropTypes.string,
  offline: PropTypes.bool,
  redirectUri: PropTypes.string,
  scope: PropTypes.string,
  setAuthorized: PropTypes.func,
}

GoogleSignIn.defaultProps = {
  scope: 'profile email',
  redirectUri: 'postmessage',
  cookiePolicy: 'single_host_origin',
}


export default GoogleSignIn
