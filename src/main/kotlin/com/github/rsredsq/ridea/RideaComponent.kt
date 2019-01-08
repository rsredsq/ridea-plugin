package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.server.Server
import com.github.rsredsq.ridea.settings.RideaConfig
import com.intellij.openapi.Disposable

class RideaComponent(
  val config: RideaConfig
) : Disposable {

  val server = Server(config.host, config.port)

  init {
    println("ASDASD")
    server.start()
  }


  override fun dispose() {

  }
}