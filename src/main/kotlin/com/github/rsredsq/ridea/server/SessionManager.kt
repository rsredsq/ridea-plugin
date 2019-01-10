package com.github.rsredsq.ridea.server

import com.github.rsredsq.ridea.utils.REMOTE_FILE_KEY
import com.intellij.ide.scratch.RootType
import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.Disposable
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VfsUtil
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class RemoteFile(val session: RemoteSession, val filename: String) {

  fun setContent(content: String) {
    val project = ProjectManager.getInstance().openProjects.first()

    WriteCommandAction.runWriteCommandAction(project) {

      val folderPath = ScratchFileService.getInstance().getRootPath(RootType.findById("ridea"))

      val folder = VfsUtil.createDirectoryIfMissing(folderPath)!!

      var newFile = folder.findChild(filename)
      if (newFile == null) {
        newFile = folder.createChildData(this, filename)
      }

      newFile.setBinaryContent(content.toByteArray())

      newFile.putUserData(REMOTE_FILE_KEY, this)

      val fileEditorManager = FileEditorManager.getInstance(project)
      fileEditorManager.openFile(newFile, true)

    }
  }

  fun saveRemotely() {
    val folderPath = ScratchFileService.getInstance().getRootPath(RootType.findById("ridea"))

    val folder = VfsUtil.createDirectoryIfMissing(folderPath)!!

    var newFile = folder.findChild(filename)!!

    val document = FileDocumentManager.getInstance().getDocument(newFile)
    val data = document!!.text.toByteArray()

    val header = "save\n" + "token: $filename\n" + "data: ${data.size}\n"

    session.socket.write(ByteBuffer.wrap(header.toByteArray()))
    session.socket.write(ByteBuffer.wrap(data))
    session.socket.write(ByteBuffer.wrap("".toByteArray()))
  }
}

class RemoteSession(val socket: SocketChannel) : Disposable {

  private var file: RemoteFile? = null
  private val commandParser = CommandParser()

  @Synchronized
  fun onMessage(message: String) {
    message.split("(?<=[\\n])".toRegex())
      .forEach {
        commandParser.parseLine(it)
      }

    commandParser.command?.apply {
      if (type == InputRemoteCommandType.OPEN) {
        file = RemoteFile(this@RemoteSession, attr["token"]!!)
        file!!.setContent(attr["data-content"]!!)
      }
      commandParser.command = null
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