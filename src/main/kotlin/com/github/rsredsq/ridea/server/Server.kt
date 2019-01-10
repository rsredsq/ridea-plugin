package com.github.rsredsq.ridea.server

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.diagnostic.logger
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import kotlin.concurrent.thread

const val SERVER_TYPE = "RIdea"

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
    try {
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
    } catch (e: Exception) {
      log.warn("Exception in server thread", e)
    }
  }

class Server(host: String, port: Int) : Disposable {
  private lateinit var selector: Selector
  private lateinit var socket: ServerSocketChannel
  private lateinit var serverThread: Thread

  private val address = InetSocketAddress(host, port)

  fun start() {
    log.info("Starting server on $address")

    selector = Selector.open()
    socket = bindSocket()
    serverThread = runServerThread()
  }

  private fun bindSocket() = ServerSocketChannel.open()
    .apply {
      configureBlocking(false)
      register(selector, SelectionKey.OP_ACCEPT)
      bind(address)
    }

  private fun runServerThread() =
    thread(
      name = "RIdea-server-socket-thread",
      block = selector.serverWorkerJob
    )

  fun stop() {
    socket.close()
    selector.close()
    serverThread.join(1000)
  }

  override fun dispose() =
    stop()

}