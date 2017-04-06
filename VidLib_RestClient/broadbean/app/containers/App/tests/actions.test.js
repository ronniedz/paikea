/**
* @Author: gajo
* @Date:   2016-06-17T23:05:06-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-08T23:30:38-08:00
*/

/* eslint-disable no-unused-vars */

import expect from 'expect'

import {
  CHANGE_MAIN_SEARCH,
  CLEAR_ERROR,
  CREATE_CHILD,
  CREATE_CHILD_SUCCESS,
  LOAD_ERROR,
  RETRIEVE_AGEGROUP,
  RETRIEVE_AGEGROUP_SUCCESS,
  SENDING_REQUEST,
  SET_AUTHORIZED_BY,
  SET_USER,
  SET_USER_CHILDREN,
  SET_VID_DIMENSIONS,
} from '../constants'

import {
  changeMainSearch,
  clearError,
  createChild,
  createChildSuccess,
  loadError,
  ageGroupLoaded,
  retrieveAgeGroup,
  sendingRequest,
  setAuthorizedBy,
  setUser,
  setUserChildren,
  setVidDimensions,
} from '../actions'

describe('App Actions', () => {
  describe('Unary Actions', () => {
    const testcases = [
      [changeMainSearch, 'searchval', CHANGE_MAIN_SEARCH],
      [createChildSuccess, 'result', CREATE_CHILD_SUCCESS],
      [ageGroupLoaded, 'results', RETRIEVE_AGEGROUP_SUCCESS],
      [sendingRequest, 'sending', SENDING_REQUEST],
      [setAuthorizedBy, 'authorized', SET_AUTHORIZED_BY],
      [setUser, 'user', SET_USER],
      [setUserChildren, 'children', SET_USER_CHILDREN],
      [setVidDimensions, 'dimensions', SET_VID_DIMENSIONS],
      [clearError, , CLEAR_ERROR],
      [retrieveAgeGroup, , RETRIEVE_AGEGROUP],
    ]

    for (const test of testcases) {
      const fixture = 'fixture'
      const [func, prop, action] = test
      const expectedResult = { type: action }
      if (prop) expectedResult[prop] = fixture
      expect(func.call(this, fixture)).toEqual(expectedResult)
    }
  })

  describe('load Error', () => {
    it('should return the correct type and the error', () => {
      const fixture = {
        msg: 'Something went wrong!',
      }
      const expectedResult = {
        type: LOAD_ERROR,
        error: fixture,
      }

      expect(loadError(fixture)).toEqual(expectedResult)
    })
  })

  describe('create Child', () => {
    it('should return the correct type and create child parameters', () => {
      const fixture = {
        name: 'name',
        agegroupid: 'group_id',
      }
      const expectedResult = {
        type: CREATE_CHILD,
        ...fixture,
      }
      expect(createChild(fixture.name, fixture.agegroupid)).toEqual(expectedResult)
    })
  })
})
