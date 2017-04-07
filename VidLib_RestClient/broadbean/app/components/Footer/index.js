/**
* @Author: gajo
* @Date:   2016-06-13T21:32:59-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-06-14T23:48:47-07:00
*/

import React from 'react'

import A from 'components/A'
import styles from './styles.css'
import { FormattedMessage } from 'react-intl'
import messages from './messages'

function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <span className={styles.text}>© bean.cab</span>
        <span className={styles.spacer}>·</span>
        <A className={styles.link} href="/about">
          <FormattedMessage {...messages.about} />
        </A>
        <A className={styles.link} href="/contact">
          <FormattedMessage {...messages.contact} />
        </A>
        <span className={styles.spacer}>·</span>
        <A className={styles.link} href="/privacy">
          <FormattedMessage {...messages.privacy} />
        </A>
      </div>
    </footer>
  )
}

export default Footer
