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
package com.example.dnd

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.draganddrop.DropHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dnd.richreceiver.AttachmentsRecyclerViewAdapter
import com.example.dnd.richreceiver.AttachmentsRepo
import com.example.dnd.richreceiver.Executors
import com.example.dnd.richreceiver.MediaReceiver
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/** Main activity for the app.  */
class ExampleRichReceiver : AppCompatActivity() {
    companion object {
        private const val USE_DROP_HELPER = true
    }

    private val attachmentsRepo by lazy {
        AttachmentsRepo(this)
    }
    private val attachmentsRecyclerViewAdapter by lazy {
        AttachmentsRecyclerViewAdapter(attachmentsRepo)
    }
    private val receiver by lazy {
        MediaReceiver(attachmentsRepo, attachmentsRecyclerViewAdapter)
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                receiver.receive(this, uris)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_rich_receiver)

        val toolbar: Toolbar = findViewById(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        val list = findViewById<RecyclerView>(R.id.attachments_list).apply {
            setHasFixedSize(true)
            setAdapter(attachmentsRecyclerViewAdapter)
        }
        attachmentsRepo.list.observe(this) {
            // Hide the list if there are no attachments
            list.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        }
        attachmentsRepo.refresh()

        val textInput = findViewById<AppCompatEditText>(R.id.text_input)
        val container = findViewById<ViewGroup>(R.id.container)
        if (USE_DROP_HELPER) {
            DropHelper.configureView(
                this,
                container,
                MediaReceiver.SUPPORTED_MIME_TYPES,
                DropHelper.Options.Builder()
                    .setHighlightColor(getColor(R.color.gray))
                    .setHighlightCornerRadiusPx(16.px)
                    .addInnerEditTexts(textInput)
                    .build(),
                receiver
            )
        } else {
            // Attach this receiver to both the text field...
            ViewCompat.setOnReceiveContentListener(
                textInput,
                MediaReceiver.SUPPORTED_MIME_TYPES,
                receiver
            )
            // ... and its larger container
            ViewCompat.setOnReceiveContentListener(
                container,
                MediaReceiver.SUPPORTED_MIME_TYPES,
                receiver
            )
        }

        // Explore the possibility to support a file picker as well
        //filePicker.attach(findViewById(R.id.text_input), MyReceiver.SUPPORTED_MIME_TYPES)
        findViewById<AppCompatImageButton>(R.id.photo_picker_action).setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        }

        findViewById<AppCompatImageButton>(R.id.send).setOnClickListener {
            Toast.makeText(
                this,
                "Sent the message with ${attachmentsRepo.size} attachments!",
                Toast.LENGTH_LONG
            ).show()
            deleteAllAttachments()
        }
    }

    private fun deleteAllAttachments() {
        val attachmentsCount = attachmentsRecyclerViewAdapter.itemCount
        val deleteAllFuture: ListenableFuture<Void?> = Executors.bg().submit<Void?> {
            attachmentsRepo.deleteAll()
            null
        }
        Futures.addCallback(deleteAllFuture, object : FutureCallback<Void?> {
            override fun onSuccess(result: Void?) {
                attachmentsRecyclerViewAdapter.notifyItemRangeRemoved(0, attachmentsCount)
            }

            override fun onFailure(t: Throwable) {
                Log.e("ReceiveContentDemo", "Error deleting attachments", t)
            }
        }, Executors.main())
    }

}
