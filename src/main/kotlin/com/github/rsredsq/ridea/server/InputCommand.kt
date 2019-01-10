package com.github.rsredsq.ridea.server

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