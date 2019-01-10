package com.github.rsredsq.ridea.server

import com.intellij.util.io.toByteArray
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.charset.Charset

private val sessionManager = SessionManager.instance

fun acceptConnection(key: SelectionKey, selector: Selector) {
  val client = (key.channel() as ServerSocketChannel).accept()
  client.configureBlocking(false)

  client.register(selector, SelectionKey.OP_READ)

  val serverInfo = "RIdea\n"
  client.write(ByteBuffer.wrap(serverInfo.toByteArray()))

  sessionManager.onNewConnection(client)

  println("Accepted connection from ${client.remoteAddress}")
}

fun readSelectionKey(key: SelectionKey) {
  val client = key.channel() as SocketChannel
  val buffer = ByteBuffer.allocate(1024)

  val message = buildString {
    while (client.read(buffer) > 0) {
      buffer.flip()
      append(buffer.toByteArray().toString(Charset.defaultCharset()))
      buffer.clear()
    }
  }

  sessionManager.onMessage(client, message)
}