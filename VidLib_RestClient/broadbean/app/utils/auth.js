/* eslint-disable */
// const request = require('./request')
import request from './request'

import {
  agegroup,
  auth,
} from 'siteconfig'

let tokenData,
  storekey = 'beancab'


export const beanToken = (data) => {
  if (data && window) {
    tokenData = data
    localStorage.setItem(storekey, JSON.stringify(tokenData))
    return tokenData
  }
  
  tokenData = JSON.parse(localStorage.getItem(storekey))

  if(tokenData) {
    const expiresAt = new Date().getTime() + (tokenData.expires_in * 1000)
    if (new Date(expiresAt) < new Date()) {
      return false
    }
  }
  return tokenData
}
