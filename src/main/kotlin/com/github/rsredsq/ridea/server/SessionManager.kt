package com.github.rsredsq.ridea.server

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import java.nio.channels.SocketChannel


class RemoteFile(val filename: String) {

  fun setContent(content: String) {
    println("CONTENT BEGIN")
    println(content)
    println("CONTENT END")

    val application = ApplicationManager.getApplication()
    val project = ProjectManager.getInstance().openProjects.first()

    WriteCommandAction.runWriteCommandAction(project) {
      val folder = LocalFileSystem.getInstance()
        .refreshAndFindFileByPath(PathManager.getTempPath())!!

      var newFile = folder.findChild(filename)
      if (newFile == null) {
        newFile = folder.createChildData(this, filename)
      }

      newFile.setBinaryContent(content.toByteArray())

      val fileEditorManager = FileEditorManager.getInstance(project)
      val e = fileEditorManager.openFile(newFile, true)

      val editor = e.first() as TextEditor

      editor.editor.document.addDocumentListener(object : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
          println(event)
        }

        override fun beforeDocumentChange(event: DocumentEvent) {
          println(event)
        }
      })

      println(e.first())

    }
  }
}

enum class CommandType {
  NONE,
  OPEN
}

data class Command(val type: CommandType, val attr: MutableMap<String, String> = mutableMapOf()) {
  companion object {
    val NONE = Command(CommandType.NONE)
  }
}

interface LineByLineParser<T : Command> {
  fun parseLine(line: String)

  val isFinished: Boolean

  val command: Command
}

class OpenCommandParser : LineByLineParser<Command> {
  override var isFinished: Boolean = false

  override val command = Command(CommandType.OPEN)

  private var dataBlockStarted = false
  private var bytesToRead: Int? = null

  override fun parseLine(line: String) {
    if (line.isEmpty()) return
    if (!dataBlockStarted) {
      val (key, value) = line.split(": ").map { it.trim() }
      command.attr[key] = value
      if (key == "data") {
        dataBlockStarted = true
        bytesToRead = value.toInt()
      }
    } else {
      val contentLine = line.substring(0, Math.min(line.length, bytesToRead!!))
      bytesToRead = bytesToRead?.minus(contentLine.length)
      command.attr.compute("data-content") { _, v ->
        if (v != null) return@compute v + line
        return@compute line
      }
      if (bytesToRead == 0) {
        isFinished = true
      }
    }
  }
}

class CommandParser {
  private var parser: LineByLineParser<Command>? = null

  var command: Command? = null

  fun parseLine(line: String) {
    parser?.apply {
      parseLine(line)
      if (isFinished) {
        parser = null
        this@CommandParser.command = command
      }
    }
    when (line.trim()) {
      "open" -> parser = OpenCommandParser()
    }
  }
}

class SocketSession(val socket: SocketChannel) {
  private val file = RemoteFile("test.java")
  private val commandParser = CommandParser()

  @Synchronized
  fun onMessage(message: String) {
    message.split("(?<=[\\n])".toRegex())
      .forEach {
        commandParser.parseLine(it)
      }

    commandParser.command?.apply {
      if (type == CommandType.OPEN) {
        file.setContent(attr["data-content"]!!)
      }
      commandParser.command = null
    }
  }
}

class SessionManager {
  private val sessions = mutableMapOf<SocketChannel, SocketSession>()

  fun newSession(socket: SocketChannel) {
    sessions[socket] = SocketSession(socket)
  }

  fun onMessage(socket: SocketChannel, message: String) {
    val session = sessions[socket]!!
    session.onMessage(message)
  }

  companion object {
    val instance: SessionManager
      get() = ServiceManager.getService(SessionManager::class.java)
  }
}