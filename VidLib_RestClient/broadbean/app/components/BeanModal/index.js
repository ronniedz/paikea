/**
* @Author: gajo
* @Date:   2016-12-16T22:01:52-08:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-16T22:02:07-08:00
*/

/**
*
* BeanModal
*
*/

import React, { Component, PropTypes } from 'react'
import styles from './styles.css'
import Modal from 'react-modal'


class BeanModal extends Component {
  constructor(props) {
    super(props)

    this.state = {
      overlay: {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(255, 255, 255, 0.75)',
        zIndex: 200,
      },
      content: {
        displays: 'inline-block',
        marginTop: '70px',
        marginLeft: 'auto',
        marginRight: 'auto',
        height: '150px',
        maxWidth: '500px',
        minWidth: '200px',
        border: '1px solid #ccc',
        background: '#fff',
        overflow: 'auto',
        WebkitOverflowScrolling: 'touch',
        borderRadius: '4px',
        outline: 'none',
        padding: '20px',
        zIndex: 200,
      },
    }
  }

  render() {
    const { children, isOpen, ...initstate } = this.props
    const { content } = this.state
    const mergedstate = { ...this.state, content: { ...content, ...initstate } }
    return (
      <Modal style={mergedstate} {...{ isOpen }} className={styles.beanModal}>
        {children}
      </Modal>
    )
  }
}

BeanModal.propTypes = {
  children: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
  isOpen: PropTypes.bool,
}

export default BeanModal
