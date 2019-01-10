package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.server.Server
import com.github.rsredsq.ridea.settings.RideaConfig
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer

class RideaComponent(
  val config: RideaConfig
) : Disposable {

  val log = logger<RideaComponent>()

  val server = Server(config.host, config.port)

  init {
    Disposer.register(this, server)
    if (config.runOnStartup) {
      server.start()
    }
  }

  override fun dispose() {
  }
}