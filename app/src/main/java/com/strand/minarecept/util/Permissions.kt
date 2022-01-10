package com.strand.minarecept.util

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.android.gms.tasks.Task

@Composable
fun permissionGrant(onGrant: () -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) onGrant()
    }
}

class OpenDocumentActivityResult(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Uri?>,
    val uri: Uri?
) {
    fun launch(mimeType: Array<String>) {
        launcher.launch(mimeType)
    }
}

@Composable
fun rememberOpenDocumentActivityResult(
    initialUri: Uri? = null,
    onSuccess: (Task<Uri>) -> Unit,
    onError: () -> Unit
): OpenDocumentActivityResult {
    var uri by rememberSaveable { mutableStateOf<Uri?>(initialUri) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            it.uploadToFirebaseAndGetRef().addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess(task) else onError()
            }
            uri = it
        }
        else {
            onError()
        }
    }
    return remember(launcher, uri) {
        OpenDocumentActivityResult(launcher, uri)
    }
}