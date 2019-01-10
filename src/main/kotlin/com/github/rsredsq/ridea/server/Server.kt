package com.github.rsredsq.ridea.server

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.diagnostic.logger
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import kotlin.concurrent.thread

private val log = logger<Server>()

private fun handleSelectionKey(key: SelectionKey) = when {
  key.isAcceptable -> {
    log.info("Accepting connection")
    acceptConnection(key, key.selector())
  }
  key.isReadable -> {
    log.debug { "Accepting connection" }
    readSelectionKey(key)
  }
  else -> {
  }
}

private val Selector.serverWorkerJob
  get() = {
    while (isOpen) {
      select()
      val keys = selectedKeys()
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

class Server(private val host: String, private val port: Int) : Disposable {
  private val selector = Selector.open()
  private val socket = ServerSocketChannel.open().apply {
    configureBlocking(false)
    register(selector, SelectionKey.OP_ACCEPT)
  }

  private val serverThread: Thread =
    thread(name = "RIdea-server-socket-thread", start = false, block = selector.serverWorkerJob)

  fun start() {
    val address = InetSocketAddress(host, port)
    log.info("Binding server to $address")
    socket.bind(address)
    serverThread.start()
  }

  override fun dispose() {
    socket.close()
    selector.close()
    serverThread.join(1000)
  }

}