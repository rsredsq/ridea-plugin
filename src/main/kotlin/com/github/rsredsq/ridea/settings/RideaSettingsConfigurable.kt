package com.github.rsredsq.ridea.settings

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.layout.panel
import javax.swing.JComponent

class RideaSettingsConfigurable : Configurable {

  private val config = ServiceManager.getService(RideaConfig::class.java)

  private val portField = JBIntSpinner(config.port, 0, 65535)
  private val runOnStartupField = JBCheckBox("Run on startup", config.runOnStartup)

  private fun isValid(): Boolean = hostField.isFieldValid

  override fun isModified(): Boolean = portField.number != config.port
      || runOnStartupField.isSelected != config.runOnStartup
      || hostField.text != config.host

  override fun getDisplayName(): String = "RIdea"

  override fun apply() {
    if (!isValid()) throw ConfigurationException("Configuration not valid")
    config.apply {
      port = portField.number
      runOnStartup = runOnStartupField.isSelected
      host = hostField.text
    }
  }

  override fun createComponent(): JComponent = panel {
    row("Remote port") { portField() }
    row { runOnStartupField() }
    row("Address to listen on: ") { hostField() }
  }

}