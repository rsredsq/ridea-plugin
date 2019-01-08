package com.github.rsredsq.ridea.server

import com.intellij.openapi.diagnostic.logger
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import kotlin.concurrent.thread

class Server(val host: String, val port: Int) {

  private val log = logger<Server>()

  private val selector = Selector.open()
  private val socket = ServerSocketChannel.open().apply {
    configureBlocking(false)
    register(selector, SelectionKey.OP_ACCEPT)
  }

  fun start() {
    socket.bind(InetSocketAddress(host, port))
    startSocketThread()
  }

  private fun startSocketThread() =
    thread(name = "RIdea-server-socket-thread") {
      println("Server thread started")
      while (true) {
        selector.select()
        val keys = selector.selectedKeys()
        val iter = keys.iterator()
        while (iter.hasNext()) {
          val key = iter.next()
          val res = runCatching { handleSelectionKey(key) }
          res.onFailure {
            it.printStackTrace()
            log.error("Error during handling socket", it)
          }
          iter.remove()
        }
      }
    }

  private fun handleSelectionKey(key: SelectionKey) = when {
    key.isAcceptable -> {
      log.info("Accepting connection")
      println("Accepting connection")
      acceptConnection(key, selector)
    }
    key.isReadable -> {
      println("Reading connection")
      readSelectionKey(key)
    }
    else -> {
    }
  }

}