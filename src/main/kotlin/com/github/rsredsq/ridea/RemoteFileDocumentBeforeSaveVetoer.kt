package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.utils.REMOTE_FILE_KEY
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer

class RemoteFileDocumentBeforeSaveVetoer(
  val remoteFilesService: RemoteFilesService,
  val remoteFilesRootType: RemoteFilesRootType,
  val fileDocuementManager: FileDocumentManager
) : FileDocumentSynchronizationVetoer() {
  override fun maySaveDocument(document: Document, isSaveExplicit: Boolean): Boolean {
    val file = fileDocuementManager.getFile(document)
    if (!remoteFilesRootType.containsFile(file)) return true

    file!!

    if (isSaveExplicit) {
      val remoteFile = file.getUserData(REMOTE_FILE_KEY)
      remoteFile?.saveRemotely()
    }
    return true
  }

}