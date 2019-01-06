package com.github.rsredsq.ridea.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBTextField
import javax.swing.JComponent
import javax.swing.event.DocumentEvent

private val ipPattern = (
    "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
    ).toPattern()

internal val hostField: JBTextField = JBTextField().apply {
  text = ServiceManager.getService(RideaConfig::class.java).host
  document.addDocumentListener(object : DocumentAdapter() {
    override fun textChanged(e: DocumentEvent) {
      hostValidator.revalidate()
    }
  })
}

private val hostValidator =
  ComponentValidator(ApplicationManager.getApplication())
    .withValidator { v ->
      val text = hostField.text
      if (ipPattern.matcher(text).matches()) {
        v.updateInfo(null)
      } else {
        v.updateInfo(
          ValidationInfo(
            "Not a valid IPv4 address",
            hostField
          )
        )
      }
    }.installOn(hostField)


val JComponent.isFieldValid: Boolean
  get() {
    return getClientProperty("JComponent.outline") == null
  }