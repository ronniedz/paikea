import 'whatwg-fetch'
import { beanToken } from './auth'
import R from 'ramda'

/**
 * Parses the JSON returned by a network request
 *
 * @param  {object} response A response from a network request
 *
 * @return {object}          The parsed JSON from the request
 */
function parseJSON(response) {
  return response.json()
}

/**
 * Checks if a network request came back fine, and throws an error if not
 *
 * @param  {objct} response   A response from a network request
 *
 * @return {object|undefined} Returns either the response, or throws an error
 */
function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response
  }

  const error = new Error(response.statusText)
  error.response = response
  throw error
}

export const authToken = () => {
  const beantoken = beanToken()
  return { 'Authorization': `${beantoken.token_type} ${beantoken.access_token}`}
}

const defaults = {
  headers: {'Content-Type': 'application/json'},
}
export const httpOptions = R.map(e => R.mergeWith((a, b) => R.merge(a, b), defaults, e),
  {
    delete: {
      method: 'DELETE',
    },
    post: {
      method: 'POST',
    },
    patch: {
      method: 'PATCH',
    },
    patchmin: {
      method: 'PATCH',
      headers: {
        Prefer: 'return=minimal',
      },
    },
    auth: {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    },
  }
)

/**
 * Requests a URL, returning a promise
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 *
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(url, options) {
  return fetch(url, options)
    .then(checkStatus)
    .then(parseJSON)
    .then((data) => ({ data }))
    .catch((err) => ({ err }))
}
