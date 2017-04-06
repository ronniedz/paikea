/**
* @Author: gajo
* @Date:   2016-07-20T21:20:08-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-07-21T00:51:27-07:00
*/

import React from 'react'

function ErrorMessage(props) {
  return (
    <div className="form__error-wrapper js-form__err-animation">
      <p className="form__error">
        {props.error}
      </p>
    </div>
  )
}

ErrorMessage.propTypes = {
  error: React.PropTypes.string,
}

export default ErrorMessage
