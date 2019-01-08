package com.github.rsredsq.ridea

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer
import com.intellij.openapi.vfs.VirtualFile

class MyVetoer : FileDocumentSynchronizationVetoer() {
  override fun maySaveDocument(document: Document, isSaveExplicit: Boolean): Boolean {
    return false
  }

}