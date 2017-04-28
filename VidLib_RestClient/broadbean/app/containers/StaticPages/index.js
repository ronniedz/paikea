/*
 *
 * StaticPages
 *
 */

import React, { PropTypes } from 'react'
import { connect } from 'react-redux'
import styles from './styles.css'
import { selectPageContent } from './selectors'
import { createStructuredSelector } from 'reselect'
import { requestHTML } from './actions'

export class StaticPages extends React.Component { // eslint-disable-line react/prefer-stateless-function
  componentDidMount() {
    this.props.dispatch(requestHTML(this.props.location.pathname))
  }

  render() {
    return (
      <div className={styles.staticPages}>
        {this.props.pagecontent}
      </div>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  pagecontent: selectPageContent(),
})

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
  }
}

StaticPages.propTypes = {
  dispatch: PropTypes.func,
  location: PropTypes.shape({
    pathname: PropTypes.string,
  }),
  pagecontent: PropTypes.string,
}


export default connect(mapStateToProps, mapDispatchToProps)(StaticPages)
