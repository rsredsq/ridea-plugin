package com.github.rsredsq.ridea.server

import com.github.rsredsq.ridea.utils.toByteBuffer
import com.github.rsredsq.ridea.utils.withNewLine
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.io.toByteArray
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

private val log = logger<Server>()
private val sessionManager = SessionManager.instance

const val SERVER_TYPE = "RIdea"

internal fun acceptConnection(key: SelectionKey, selector: Selector) {
  fun sendServerInfo(client: SocketChannel) =
    client.write(SERVER_TYPE.withNewLine().toByteBuffer())

  val client = (key.channel() as ServerSocketChannel).accept()
  client.configureBlocking(false)

  client.register(selector, SelectionKey.OP_READ)

  sendServerInfo(client)

  sessionManager.onNewConnection(client)

  log.info("Accepted new connection from ${client.remoteAddress}")
}

internal fun readSelectionKey(key: SelectionKey) {
  val client = key.channel() as SocketChannel
  val buffer = ByteBuffer.allocate(1024)

  //since the text protocol is used, then convert everything into string
  val message = buildString {
    while (client.read(buffer) > 0) {
      buffer.flip()
      append(buffer.toByteArray().toString(Charset.defaultCharset()))
      buffer.clear()
    }
  }

  sessionManager.onMessage(client, message)
}