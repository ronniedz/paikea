/**
* @Author: gajo
* @Date:   2016-06-21T16:53:00-07:00
* @Last modified by:   gajo
* @Last modified time: 2016-12-08T12:01:57-08:00
*/

/* eslint-disable new-cap */

/**
*
* NavMenu
*
*/

import React, { PropTypes, Component } from 'react'
import styles from './styles.css'
import BeanModal from 'components/BeanModal'
import { List } from 'immutable'
import { FormattedMessage, injectIntl, intlShape } from 'react-intl'
import messages from './messages'

class NavMenu extends Component {
  constructor(props) {
    super(props)
    this.state = {
      modal: false,
    }
    this.createChildDialog = this.createChildDialog.bind(this)
    this.closeChildDialog = this.closeChildDialog.bind(this)
    this.validateChildSubmission = this.validateChildSubmission.bind(this)
  }

  offSpringMenu(child, changeRoute, onNavigateChildList) {
    return child ?
      child.map((ea, i) => (
        <button
          data-id={ea.id}
          className={styles.kidbutn}
          key={i}
          onClick={onNavigateChildList}
        >
          {ea.name}
        </button>
      ))
      : null
  }

  createChildDialog() {
    this.setState({ modal: true })
  }

  closeChildDialog() {
    this.setState({ modal: false })
  }

  validateChildSubmission(currenttarget) {
    const name = currenttarget.querySelector('input[type="text"]')
    const agegroupid = currenttarget.querySelector('input[type="radio"][name="agegroup"]:checked')
    if (name && name.value && agegroupid && agegroupid.value) {
      this.props.onCreateChild(name.value, agegroupid.value)
      this.setState({ modal: false })
    } else {
      alert('Please fill in all values')
    }
  }

  renderOffspringForm(authby, onNavigateChildList, userchildren, changeRoute) {
    if (!authby) return null
    return this.offSpringMenu(userchildren, changeRoute, onNavigateChildList)
  }

  renderAgeGroup(ages) {
    if (!ages) return null

    return ages.map((age, i) => (
      <label key={i} >
        <input type="radio" name="agegroup" value={age.id} />{age.label}
      </label>
    ))
  }

  renderCreateChildButton(authby) {
    if (!authby) return null
    return (
      <button onClick={this.createChildDialog}>
        <FormattedMessage {...messages.createChild} />
      </button>
    )
  }

  render() {
    const {
      agegroup,
      authby,
      changeRoute,
      intl,
      onChangeMainSearch,
      onNavigateChildList,
      onSearchSubmitForm,
      submitval,
      userchildren,
    } = this.props
    const { modal } = this.state

    return (
      <div className={styles.asfixedheader}>
        <div className={styles.menusctn}>
          <a
            onClick={(evt) => {
              evt.preventDefault()
              changeRoute('/')
            }}
            href="/"
          >
            <FormattedMessage {...messages.recommendations} />
          </a>
        </div>

        <div className={styles.menusctn}>
          <form id="searchsubmit" onSubmit={onSearchSubmitForm} >
            <input
              id="searchinput"
              className={styles.searchinput}
              value={submitval}
              placeholder={intl.messages[messages.search.placeholder]}
              type="text"
              onChange={onChangeMainSearch}
            />
            <input type="submit" value={intl.messages[messages.search.submit]} />
          </form>
        </div>
        <div className={styles.menusctn_offspring}>
          {authby &&
            <div className={styles.menusctn}>
              {this.renderCreateChildButton(authby)}
            </div>
          }
          {userchildren.size > 0 &&
            <div className={styles.menusctn}>
              {this.renderOffspringForm(authby, onNavigateChildList, userchildren, changeRoute)}
            </div>
          }
        </div>
        <BeanModal height="170px" isOpen={modal}>
          <button onClick={this.closeChildDialog}>close</button>
          <div>
            <form
              onSubmit={
                (evt) => {
                  evt.preventDefault()
                  this.validateChildSubmission(evt.currentTarget)
                }
              }
            >
              <label>Name<input className={styles.textinput} type="text" /></label>
              <br />
              {this.renderAgeGroup(agegroup)}
              <br />
              <input type="submit" />
            </form>
          </div>
        </BeanModal>
      </div>
    )
  }
}

NavMenu.propTypes = {
  agegroup: PropTypes.instanceOf(List),
  authby: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  changeRoute: React.PropTypes.func,
  intl: intlShape,
  onCreateChild: PropTypes.func,
  onNavigateChildList: PropTypes.func,
  onSearchSubmitForm: PropTypes.func,
  onChangeMainSearch: PropTypes.func,
  submitval: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
  userchildren: PropTypes.instanceOf(List),
}

export default injectIntl(NavMenu)
