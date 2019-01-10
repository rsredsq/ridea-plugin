package com.github.rsredsq.ridea.server

import com.github.rsredsq.ridea.RemoteFilesService
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import java.nio.channels.SocketChannel

val NEW_LINE_SPLIT_KEEP_SEPARATOR_REGEX = "(?<=[\\n])".toRegex()

class RemoteSession(val socket: SocketChannel) : Disposable {

  private val commandParser = CommandParser()

  @Synchronized
  fun onMessage(message: String) {
    message
      .split(NEW_LINE_SPLIT_KEEP_SEPARATOR_REGEX)
      .forEach {
        commandParser.parseLine(it)
      }

    commandParser.command?.apply {
      if (type == InputRemoteCommandType.OPEN) {
        val filename = attr[OPEN_TOKEN]!!
        val content = attr[OPEN_DATA_CONTENT]!!
        RemoteFilesService.instance.keepRemoteFileLocally(this@RemoteSession, filename, content)
      }
    }
  }

  override fun dispose() {
  }
}

class SessionManager {
  private val sessions = mutableMapOf<SocketChannel, RemoteSession>()

  fun onNewConnection(socket: SocketChannel) {
    sessions[socket] = RemoteSession(socket)
  }

  fun onMessage(socket: SocketChannel, message: String) =
    sessions[socket]?.apply {
      onMessage(message)
    }

  fun onCloseConnection(socket: SocketChannel) {
    sessions.remove(socket)
    runCatching { socket.close() }
      .onFailure {
        println("Unable to close socket")
      }

  }

  companion object {
    val instance: SessionManager
      get() = ServiceManager.getService(SessionManager::class.java)
  }
}