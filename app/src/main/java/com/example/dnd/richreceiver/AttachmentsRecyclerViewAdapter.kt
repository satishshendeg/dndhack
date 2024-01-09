package com.example.dnd.richreceiver

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dnd.R

internal class AttachmentsRecyclerViewAdapter(attachments: List<Uri>?) :
    RecyclerView.Adapter<AttachmentsRecyclerViewAdapter.MyViewHolder>() {
    internal class MyViewHolder(var mAttachmentThumbnailView: AppCompatImageView) :
        RecyclerView.ViewHolder(
            mAttachmentThumbnailView
        )

    private val mAttachments: MutableList<Uri>

    init {
        mAttachments = ArrayList(attachments)
    }

    fun addAttachments(uris: List<Uri>) {
        mAttachments.addAll(uris)
    }

    fun clearAttachments() {
        mAttachments.clear()
    }

    override fun getItemCount(): Int {
        return mAttachments.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attachment, parent, false) as AppCompatImageView
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val uri = mAttachments[position]
        holder.mAttachmentThumbnailView.setImageURI(uri)
        holder.mAttachmentThumbnailView.clipToOutline = true
    }
}