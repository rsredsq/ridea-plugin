package com.github.rsredsq.ridea.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
  name = "RideaConfig",
  storages = [Storage("RIdeaConfig.xml")]
)
data class RideaConfig(
  var port: Int = 52698,
  var runOnStartup: Boolean = true,
  var host: String = "127.0.0.1"
) : PersistentStateComponent<RideaConfig> {

  override fun getState(): RideaConfig = this

  override fun loadState(state: RideaConfig) =
    XmlSerializerUtil.copyBean(state, this)

}