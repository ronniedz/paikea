/* eslint-disable */
// const request = require('./request')
import request from './request'

import {
  agegroup,
  auth,
} from 'siteconfig'


function prepareAuthRequest(token, cb) {
  sendAuthRequest(auth.endpoint, {
    id_token: token ,
    ...head.auth,
  })
}


function sendAuthRequest(url, options) {
  // const request = require('./request')
  console.log('options', options)

  request(url, options)
    .then((res) => console.log('res', res))
}

let tokenData
export const beanToken = (data) => {
  if (data) { tokenData = data }
  return tokenData
}

export default {
  beanlogin(token) {

    console.log('token', token)

    return new Promise( (resolve, reject) => {
      // if (localStorage.token) {
      //   resolve(true)
      // }

      // pretendRequest(email, password, (res) => {
      prepareAuthRequest(token, (res) => {
        if (res.authenticated) {
          localStorage.token = res.token
          resolve(true)
        } else {
          resolve(false)
        }
      })
    })

},
  getToken: function () {
    return localStorage.token
  },

  logout: function (cb) {
    delete localStorage.token
    if (cb) cb()
  },

  loggedIn: function () {
    return !!localStorage.token
  },
}


function pretendRequest(email, pass, cb) {
  setTimeout(() => {
    if (email === 'garrickajo@gmail.com' && pass === 'HOW_TO_SAFELY_IMPLEMENT_PASSWORD?') {
      cb({
        authenticated: true,
        token: Math.random().toString(36).substring(7)
      })
    } else {
      cb({ authenticated: false })
    }
  }, 500)
}
