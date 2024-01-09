package com.example.dnd.richreceiver

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dnd.R

internal class AttachmentsRecyclerViewAdapter(attachments: List<Uri>?) :
    RecyclerView.Adapter<AttachmentsRecyclerViewAdapter.MyViewHolder>() {
    internal class MyViewHolder(var attachmentThumbnailView: AppCompatImageView) :
        RecyclerView.ViewHolder(
            attachmentThumbnailView
        )

    private val attachments: MutableList<Uri>

    init {
        this.attachments = ArrayList(attachments)
    }

    fun addAttachments(uris: List<Uri>) {
        attachments.addAll(uris)
    }

    fun clearAttachments() {
        attachments.clear()
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attachment, parent, false) as AppCompatImageView
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val uri = attachments[position]
        holder.attachmentThumbnailView.setImageURI(uri)
        holder.attachmentThumbnailView.clipToOutline = true
    }
}