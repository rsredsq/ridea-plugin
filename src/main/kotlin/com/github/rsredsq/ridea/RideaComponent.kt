package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.server.Server
import com.github.rsredsq.ridea.settings.RideaConfig
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer

class RideaComponent(
  config: RideaConfig
) : Disposable {

  private val server = Server(config.host, config.port)

  var isServerStarted: Boolean = false
    private set

  init {
    Disposer.register(this, server)
    if (config.runOnStartup) {
      serverStart()
    }
  }

  fun serverStart() {
    if (!isServerStarted) {
      server.start()
      isServerStarted = true
    }
  }

  fun serverStop() {
    if (isServerStarted) {
      server.stop()
      isServerStarted = false
    }
  }

  override fun dispose() {
  }

  companion object {
    val instance: RideaComponent
      get() = ApplicationManager.getApplication().getComponent(RideaComponent::class.java)
  }
}