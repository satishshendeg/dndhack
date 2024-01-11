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

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.dnd.richreceiver.Executors.bg
import com.example.dnd.richreceiver.Executors.main
import androidx.core.view.ContentInfoCompat
import androidx.core.view.OnReceiveContentListener
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.io.FileNotFoundException

/**
 * Sample [OnReceiveContentListener] implementation that accepts all URIs, and delegates
 * handling for all other content to the platform.
 */
internal class MediaReceiver(
    private val attachmentsRepo: AttachmentsRepo,
    private val attachmentsRecyclerViewAdapter: AttachmentsRecyclerViewAdapter
) : OnReceiveContentListener {
    companion object {
        val SUPPORTED_MIME_TYPES = arrayOf("image/*")
        private fun collectUris(clip: ClipData): List<Uri> {
            val uris: MutableList<Uri> = ArrayList(clip.itemCount)
            for (i in 0 until clip.itemCount) {
                val uri = clip.getItemAt(i).uri
                if (uri != null) {
                    uris.add(uri)
                }
            }
            return uris
        }
    }

    override fun onReceiveContent(
        view: View,
        payload: ContentInfoCompat
    ): ContentInfoCompat? {
        // Split the incoming content into two groups: content URIs and everything else.
        // This way we can implement custom handling for URIs and delegate the rest.
        val split = payload.partition { item: ClipData.Item -> item.uri != null }
        val uriContent = split.first
        val remaining = split.second
        if (uriContent != null) {
            receive(view.context, uriContent)
        }
        // Return anything that we didn't handle ourselves. This preserves the default platform
        // behavior for text and anything else for which we are not implementing custom handling.
        return remaining
    }

    /**
     * Handles incoming content URIs. If the content is an image, stores it as an attachment in the
     * app's private storage. If the content is any other type, simply shows a toast with the type
     * of the content and its size in bytes.
     *
     *
     * **Important:** It is significant that we pass along the `payload`
     * object to the worker thread that will process the content, because URI permissions are tied
     * to the payload object's lifecycle. If that object is not passed along, it could be garbage
     * collected and permissions would be revoked prematurely (before we have a chance to process
     * the content).
     */
    private fun receive(context: Context, payload: ContentInfoCompat) {
        receive(context, collectUris(payload.clip))
    }

    fun receive(context: Context, uris: List<Uri>) {
        val applicationContext = context.applicationContext
        val addAttachmentsFuture = bg().submit<List<Uri>> {
            val localUris: MutableList<Uri> = ArrayList(uris.size)
            for (uri in uris) {
                val mimeType = applicationContext.contentResolver.getType(uri)
                Log.i("ReceiveContentDemo", "Processing URI: $uri (type: $mimeType)")
                if (ClipDescription.compareMimeTypes(mimeType, "image/*")) {
                    // Read the image at the given URI and write it to private storage.
                    localUris.add(attachmentsRepo.write(uri))
                } else {
                    showMessage(applicationContext, uri, mimeType!!)
                }
            }
            localUris
        }
        Futures.addCallback(addAttachmentsFuture, object : FutureCallback<List<Uri>> {
            override fun onSuccess(localUris: List<Uri>) {
                // Show the image in the UI by passing the URI pointing to the locally stored copy
                // to the recycler view adapter.
                attachmentsRecyclerViewAdapter.notifyDataSetChanged()
                Log.i("ReceiveContentDemo", "Processed content: $localUris")
            }

            override fun onFailure(t: Throwable) {
                Log.e("ReceiveContentDemo", "Error processing content", t)
            }
        }, main())
    }

    /**
     * Reads the size of the given content URI and shows a toast with the type of the content and
     * its size in bytes.
     */
    private fun showMessage(
        applicationContext: Context,
        uri: Uri, mimeType: String
    ) {
        bg().execute {
            val contentResolver = applicationContext.contentResolver
            val lengthBytes: Long
            var fd: AssetFileDescriptor? = null
            lengthBytes = try {
                fd = contentResolver.openAssetFileDescriptor(uri, "r")
                fd!!.length
            } catch (e: FileNotFoundException) {
                Log.e("ReceiveContentDemo", "Error opening content URI: $uri", e)
                return@execute
            } finally {
                fd?.close()
            }
            val msg = "Content of type $mimeType ($lengthBytes bytes): $uri"
            Log.i("ReceiveContentDemo", msg)
            main().execute { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
        }
    }

}