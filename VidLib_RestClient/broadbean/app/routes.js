/**
* @Author: gajo
* @Date:   2016-06-15T16:28:19-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-11-11T01:09:09-08:00
*/

/* eslint-disable no-console */

// These are the pages you can go to.
// They are all wrapped in the App component, which should contain the navbar etc
// See http://blog.mxstbr.com/2016/01/react-apps-with-pages for more information
// about the code splitting business
import { getHooks } from './utils/hooks'
import { beanToken } from './utils/auth'
import clientpaths from './clientpaths.json'
import R from 'ramda'

const [staticRoutes, dynamicRoutes] = R.partition(e => e.hasOwnProperty('file'))(clientpaths.routes)

const errorLoading = (err) => {
  console.error('Dynamic page loading failed', err) // eslint-disable-line no-console
}

const loadModule = (cb) => (componentModule) => {
  cb(null, componentModule.default)
}

function redirectToHomePage(nextState, replace) {
  if (!beanToken()) {
    console.warn('not logged in. accessing unauthorized area')
    // replace({
    //   pathname: '/',
    //   state: { nextPathname: nextState.location.pathname },
    // })
  }
}


export default function createRoutes(store) {
  const { injectReducer, injectSagas } = getHooks(store)
  return [
    {
      path: '/',
      name: 'home',
      getComponent(nextState, cb) {
        const importModules = Promise.all([
          System.import('containers/HomePage/reducer'),
          System.import('containers/HomePage/sagas'),
          System.import('containers/HomePage'),
        ])

        const renderRoute = loadModule(cb)

        importModules.then(([reducer, sagas, component]) => {
          injectReducer('home', reducer.default)
          injectSagas(sagas.default)

          renderRoute(component)
        })

        importModules.catch(errorLoading)
      },
    },
    {
      path: '/search',
      name: 'search',
      getComponent(nextState, cb) {
        const importModules = Promise.all([
          System.import('containers/SearchPage/reducer'),
          System.import('containers/SearchPage/sagas'),
          System.import('containers/SearchPage'),
        ])

        const renderRoute = loadModule(cb)

        importModules.then(([reducer, sagas, component]) => {
          injectReducer('search', reducer.default)
          injectSagas(sagas.default)
          renderRoute(component)
        })

        importModules.catch(errorLoading)
      },
    },
    {
      onEnter: redirectToHomePage,
      path: '/offspring(/:id)',
      name: 'offspring',
      getComponent(nextState, cb) {
        const importModules = Promise.all([
          System.import('containers/OffspringPage/reducer'),
          System.import('containers/OffspringPage/sagas'),
          System.import('containers/OffspringPage'),
        ])

        const renderRoute = loadModule(cb)

        importModules.then(([reducer, sagas, component]) => {
          injectReducer('offspring', reducer.default)
          injectSagas(sagas.default)
          renderRoute(component)
        })

        importModules.catch(errorLoading)
      },
    },
    {
      path: '*',

      name: 'staticPages',
      getComponent(nextState, cb) {
        const pathname = nextState.location.pathname

        const staticrouter = R.ifElse(
          R.contains(pathname),
          () => {
            const importModules = Promise.all([
              System.import('containers/StaticPages/reducer'),
              System.import('containers/StaticPages/sagas'),
              System.import('containers/StaticPages'),
            ])

            const renderRoute = loadModule(cb)

            importModules.then(([reducer, sagas, component]) => {
              injectReducer('staticPages', reducer.default)
              injectSagas(sagas.default)
              renderRoute(component)
            })

            importModules.catch(errorLoading)
          },
          () => System.import('containers/NotFoundPage')
            .then(loadModule(cb))
            .catch(errorLoading)
        )

        const staticpaths = R.values(R.map(e => e.path)(staticRoutes))
        staticrouter(staticpaths)
      },
    },
  ]
}
