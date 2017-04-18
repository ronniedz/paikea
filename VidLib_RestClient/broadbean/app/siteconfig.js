/**
* @Author: gajo
* @Date:   2016-06-23T09:00:44-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-15T01:51:32-07:00
*/
// defaultparams: '&part=snippet&maxResults=15&order=date&publishedAfter=2006-01-01T00%3A00%3A00Z&regionCode=BR&relevanceLanguage=PT&type=video&videoCaption=any&videoDuration=long&videoLicense=youtube&videoType=any&fields=etag%2Citems%2Ckind%2CnextPageToken%2CpageInfo'

const domain = process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : ''

export const gapicid = '16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com'

export const videoassets = {
  dimensions: [
    {
      id: 426,
      upperthreshold: 640,
      height: 240,
      width: 426,
      thumbwidth: 426,
    },
    {
      id: 640,
      upperthreshold: 854,
      height: 360,
      width: 640,
      thumbwidth: 320,
    },
    {
      id: 854,
      upperthreshold: 5000,
      height: 480,
      width: 854,
      thumbwidth: 427,
    },
  ],
}

export const search = {
  endpoint: `${domain}/api/ytapi/v1/search`,

  queryprepend: 'children%2C',

  defaultparams: (regioncode = 'US', langcode = 'en', token = '', numresults = 20) => `&pageToken=${token}&part=snippet&maxResults=${numresults}&regionCode=${regioncode}&relevanceLanguage=${langcode}&type=video&fields=etag%2Citems%2Ckind%2CnextPageToken%2CpageInfo`,

  titleprepend: 'Search term: ',
}

export const recommendations = {
  endpoint: 'http://localhost:4001/recommendations',
}

export const userchildren = {
  endpoint: `${domain}/api/child`,
}

export const agegroup = {
  endpoint: `${domain}/api/age`,
}

export const video = {
  endpoint: `${domain}/api/video`,
}

export const playlist = {
  endpoint: `${domain}/api/playlist`,
}

export const auth = {
  endpoint: `${domain}/api/auth/gcallback`,
}

export const associateChildPlaylistUrl = (childId, playlistId) => `${domain}/api/child/${childId}/pl/${playlistId}`
