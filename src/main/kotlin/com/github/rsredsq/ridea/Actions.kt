package com.github.rsredsq.ridea

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ServerStartAction : AnAction() {
  private val ridea = RideaComponent.instance

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = !ridea.isServerStarted
  }

  override fun actionPerformed(e: AnActionEvent) {
    ridea.serverStart()
    Notifications.Bus.notify(
      Notification(
        "RIdea",
        "[RIdea] Server status",
        "Server has been successfully started",
        NotificationType.INFORMATION
      )
    )
  }
}

class ServerStopAction : AnAction() {
  private val ridea = RideaComponent.instance

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = ridea.isServerStarted
  }

  override fun actionPerformed(e: AnActionEvent) {
    ridea.serverStop()
    Notifications.Bus.notify(
      Notification(
        "RIdea",
        "[RIdea] Server status",
        "Server has been successfully stopped",
        NotificationType.INFORMATION
      )
    )
  }
}