package com.github.rsredsq.ridea

import com.github.rsredsq.ridea.utils.REMOTE_SESSION_KEY
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer

class RemoteFileDocumentBeforeSaveVetoer(
  private val remoteFilesService: RemoteFilesService,
  private val remoteFilesRootType: RemoteFilesRootType,
  private val fileDocuementManager: FileDocumentManager
) : FileDocumentSynchronizationVetoer() {
  override fun maySaveDocument(document: Document, isSaveExplicit: Boolean): Boolean {
    val file = fileDocuementManager.getFile(document)
    if (!remoteFilesRootType.containsFile(file)) return true

    file!!

    if (!isSaveExplicit) return true
    val remoteSession = file.getUserData(REMOTE_SESSION_KEY)!!
    val saveRes = runCatching {
      remoteFilesService.saveFileRemotely(remoteSession, file.name, document.text)
    }
    saveRes.onFailure {
      ApplicationManager.getApplication().runWriteAction {
        file.delete(this)
        FileDocumentManager.getInstance().reloadFiles(file)
      }
    }
    saveRes.onSuccess { return true }
    return false
  }
}