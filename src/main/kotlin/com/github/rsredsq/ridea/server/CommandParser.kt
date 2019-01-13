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
      parseAttribute(line)
    } else {
      parseData(line)
    }
  }

  private fun parseData(line: String) {
    val contentLine = line.substring(0, Math.min(line.length, bytesToRead!!))
    bytesToRead = bytesToRead?.minus(contentLine.length)
    command.attr.compute(OPEN_DATA_CONTENT) { _, v ->
      if (v != null) return@compute v + line
      return@compute line
    }
    if (bytesToRead == 0) {
      isFinished = true
    }
  }

  private fun parseAttribute(line: String) {
    val (key, value) = line.split(": ").map { it.trim() }
    command.attr[key] = value
    if (key == "data") {
      dataBlockStarted = true
      bytesToRead = value.toInt()
    }
  }
}

class CommandParser {
  private var parser: LineByLineParser? = null

  var command: InputCommand? = null
    private set
    get() {
      if (field != null) {
        val tmp = field
        field = null
        return tmp
      }
      return field
    }

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