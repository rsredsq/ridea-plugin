package com.github.rsredsq.ridea.server

interface LineByLineParser {
  fun parseLine(line: String)

  val isFinished: Boolean

  val command: InputCommand
}

class OpenCommandParser : LineByLineParser {
  override var isFinished: Boolean = false

  override val command = InputCommand(InputRemoteCommandType.OPEN)

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
  private var parser: LineByLineParser? = null

  var command: InputCommand? = null

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