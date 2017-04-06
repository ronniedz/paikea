/**
* @Author: gajo
* @Date:   2016-08-11T11:29:25-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-11T22:02:48-07:00
*/

/**
 * A link to a certain page, an anchor tag
 */

import React, { PropTypes } from 'react'

import styles from './styles.css'

function A(props) {
  return (
    <a
      className={
        props.className || styles.link
      }
      {...props}
    />
  )
}

A.propTypes = {
  className: PropTypes.string,
  href: PropTypes.string.isRequired,
  target: PropTypes.string,
  children: PropTypes.node.isRequired,
}

export default A
