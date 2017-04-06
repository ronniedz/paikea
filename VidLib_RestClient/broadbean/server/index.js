/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-17T16:16:08-07:00
*/

/* eslint consistent-return:0 */

const express = require('express')
const logger = require('./logger')

const argv = require('minimist')(process.argv.slice(2))
const setup = require('./middlewares/frontendMiddleware')
const isDev = process.env.NODE_ENV !== 'production'
const ngrok = (isDev && process.env.ENABLE_TUNNEL) || argv.tunnel ? require('ngrok') : false
const resolve = require('path').resolve

// If you need a backend, e.g. an API, add your custom backend-specific middleware here
// app.use('/api', myApi)

// In production we need to pass these values in instead of relying on webpack
const app = setup(express(), {
  outputPath: resolve(process.cwd(), 'build'),
  publicPath: '/',
})

// get the intended port number, use port 3000 if not provided
const port = argv.port || process.env.PORT || 5000

// Start your app.
app.listen(port, (err) => {
  if (err) {
    return logger.error(err.message)
  }

/*

  // Connect to ngrok in dev mode
  if (ngrok) {
    ngrok.connect(port, (innerErr, url) => {
      if (innerErr) {
        return logger.error(innerErr)
      }

      logger.appStarted(port, url)
    })
  } else {
    logger.appStarted(port)
  }

*/

  logger.appStarted(port)
})
