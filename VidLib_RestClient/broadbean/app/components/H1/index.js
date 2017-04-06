/**
* @Author: gajo
* @Date:   2016-08-11T11:29:25-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-08-11T22:03:12-07:00
*/

import React from 'react'

import styles from './styles.css'

function H1(props) {
  return (
    <h1 className={styles.heading1} {...props} />
  )
}

export default H1
