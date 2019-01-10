package com.github.rsredsq.ridea.server

const val OPEN_TOKEN = "token"
const val OPEN_DATA_LENGTH = "data"
const val OPEN_DATA_CONTENT = "data-content"

enum class InputRemoteCommandType {
  OPEN
}

enum class OutputRemoteCommandType {
  SAVE
}

data class InputCommand(
  val type: InputRemoteCommandType,
  val attr: MutableMap<String, String> = mutableMapOf()
)

data class OutputCommand(
  val type: OutputRemoteCommandType,
  val attr: MutableMap<String, String> = mutableMapOf()
)