/**
* @Author: gajo
* @Date:   2016-08-11T11:29:25-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-11T22:05:05-07:00
*/

/**
 * WEBPACK DLL GENERATOR
 *
 * This profile is used to cache webpack's module
 * contexts for external library and framework type
 * dependencies which will usually not change often enough
 * to warrant building them from scratch every time we use
 * the webpack process.
 */

const { join } = require('path')
const defaults = require('lodash/defaultsDeep')
const webpack = require('webpack')
const pkg = require(join(process.cwd(), 'package.json'))
const dllPlugin = require('../config').dllPlugin

if (!pkg.dllPlugin) { process.exit(0) }

const dllConfig = defaults(pkg.dllPlugin, dllPlugin.defaults)
const outputPath = join(process.cwd(), dllConfig.path)

module.exports = {
  context: process.cwd(),
  entry: dllConfig.dlls ? dllConfig.dlls : dllPlugin.entry(pkg),
  devtool: 'eval',
  output: {
    filename: '[name].dll.js',
    path: outputPath,
    library: '[name]',
  },
  plugins: [
    new webpack.DllPlugin({ name: '[name]', path: join(outputPath, '[name].json') }), // eslint-disable-line no-new
  ],
  module: {
    loaders: [{
      test: /\.json$/,
      loader: 'json-loader',
    }],
  },
}
