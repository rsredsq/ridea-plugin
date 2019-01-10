package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.server.RemoteFile
import com.github.rsredsq.ridea.server.SessionManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.vfs.VirtualFile

class RemoteFilesService(
  private val remoteFilesRootType: RemoteFilesRootType
) {

//  fun saveRemoteFile(file: RemoteFile) {
//    println("Trying to save remote file: $file")
//    file.session.saveData()
//  }

  companion object {
    val instance: RemoteFilesService
      get() = ServiceManager.getService(RemoteFilesService::class.java)
  }
}