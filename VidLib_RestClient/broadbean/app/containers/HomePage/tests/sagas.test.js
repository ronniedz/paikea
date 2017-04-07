/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-29T04:02:42-07:00
*/

/**
 * Tests for HomePage sagas
 */

/* eslint-disable no-unused-vars */

import expect from 'expect'
import { take, call, put, select, fork, cancel } from 'redux-saga/effects'
import { LOCATION_CHANGE } from 'react-router-redux'

import {
  fetchBeanRecommends,
  getBeanRecommendsWatcher,
  BeanRecommendsData,
} from '../sagas'

import { LOAD_YT_SEARCH } from 'containers/App/constants'
import { ytSearchLoaded, loadError } from 'containers/App/actions'

import request from 'utils/request'
import { selectMainSearchVal } from 'containers/App/selectors'

import { search } from 'siteconfig'

const testval = 'children learn numbers'
