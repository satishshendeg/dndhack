/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dnd.richreceiver

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.google.common.collect.ImmutableList
import com.google.common.io.ByteStreams
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**
 * Stores attachments as files in the app's private storage directory (see
 * [Context.getDataDir], [Context.getFilesDir], etc).
 */
internal class AttachmentsRepo(private val context: Context) {
    companion object {
        // This matches the name declared in AndroidManifest.xml
        private const val FILE_PROVIDER_AUTHORITY =
            "com.example.dnd.fileprovider"
    }

    val size: Int
        get() = allUris.size

    val allUris: List<Uri>
        get() {
            val files = attachmentsDir.listFiles()
            if (files == null || files.isEmpty()) {
                return ImmutableList.of()
            }
            val uris = ImmutableList.builderWithExpectedSize<Uri>(files.size)
            for (file in files) {
                uris.add(getUriForFile(file))
            }
            return uris.build()
        }

    private val attachmentsDir: File = File(context.filesDir, "attachments")

    /**
     * Reads the content at the given URI and writes it to private storage. Then returns a content
     * URI referencing the newly written file.
     */
    fun write(uri: Uri): Uri {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        try {
            contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { uri.toString() }
                attachmentsDir.mkdirs()
                // We need to add a timestamp so that the attachments remain ordered correctly
                val fileName = "${System.currentTimeMillis()}-${UUID.randomUUID()}.$ext"
                val newAttachment = File(attachmentsDir, fileName)
                FileOutputStream(newAttachment).use { output ->
                    // Copy the file
                    ByteStreams.copy(input, output)
                }
                val resultUri = getUriForFile(newAttachment)
                Log.i("AttachmentsRepo", "Saved content: originalUri=$uri, resultUri=$resultUri")
                return resultUri
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun delete(position: Int) {
        attachmentsDir.listFiles()?.getOrNull(position)?.delete()
    }

    fun deleteAll() {
        val files = attachmentsDir.listFiles() ?: return
        for (file in files) {
            file.delete()
        }
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
    }

}
