/**
 * @Author: gajo
 * @Date:   2016-12-08T22:05:25-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T21:56:31-08:00
 */

/*
 *
 * OffspringPage actions
 *
 */

import {
  CHANGE_VIDEO,
  CREATE_PLAYLIST,
  DELETE_VIDEO,
  LOAD_ERROR,
  RETRIEVE_PLAYLISTS,
  RETRIEVE_PLAYLISTS_SUCCESS,
  IS_LOOPING,
  UPDATE_OFFSPRING_PLAYLIST,
} from './constants'

export function deleteFromPlaylist(playlistid, videoid) {
  return {
    type: DELETE_VIDEO,
    playlistid,
    videoid,
  }
}

export function changeVideo(listitemobj, offspringId) {
  return {
    type: CHANGE_VIDEO,
    listitemobj,
    offspringId,
  }
}

export function createPlaylist(title, child = null) {
  return {
    type: CREATE_PLAYLIST,
    title,
    child,
  }
}

export function retrievePlaylists(childid) {
  return {
    type: RETRIEVE_PLAYLISTS,
    childid,
  }
}

export function playlistsLoaded(results) {
  return {
    type: RETRIEVE_PLAYLISTS_SUCCESS,
    results,
  }
}

export function loadError(err) {
  return {
    type: LOAD_ERROR,
    err,
  }
}

export function toggleLooping(isLooping) {
  return {
    type: IS_LOOPING,
    isLooping,
  }
}

export function updateOffspringPlaylist(updated) {
  return {
    type: UPDATE_OFFSPRING_PLAYLIST,
    updated,
  }
}
