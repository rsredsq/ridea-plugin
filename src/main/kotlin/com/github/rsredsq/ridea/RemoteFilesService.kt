package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.server.RemoteSession
import com.github.rsredsq.ridea.utils.REMOTE_SESSION_KEY
import com.github.rsredsq.ridea.utils.toByteBuffer
import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

class RemoteFilesService(
  private val remoteFilesRootType: RemoteFilesRootType
) {

  fun keepRemoteFileLocally(remoteSession: RemoteSession, filename: String, content: String) {
    //currently it'll work only if any project is open
    val project = ProjectManager.getInstance().openProjects.first()

    WriteCommandAction.runWriteCommandAction(project) {
      val file = pickVirtualFile(filename)

      file.setBinaryContent(content.toByteArray())

      file.putUserData(REMOTE_SESSION_KEY, remoteSession)

      val fileEditorManager = FileEditorManager.getInstance(project)
      fileEditorManager.openFile(file, true)
    }
  }

  fun saveFileRemotely(remoteSession: RemoteSession, filename: String, content: String) {
    val header = "save\n" + "token: $filename\n" + "data: ${content.length}\n"

    remoteSession.socket.apply {
      write(header.toByteBuffer())
      write(content.toByteBuffer())
      write("\n".toByteBuffer())
    }
  }

  private fun pickVirtualFile(filename: String): VirtualFile {
    val remoteFilesFolderPath = ScratchFileService.getInstance().getRootPath(remoteFilesRootType)
    val folder = VfsUtil.createDirectoryIfMissing(remoteFilesFolderPath)!!

    return folder.findChild(filename) ?: folder.createChildData(this, filename)
  }

  companion object {
    val instance: RemoteFilesService
      get() = ServiceManager.getService(RemoteFilesService::class.java)
  }
}