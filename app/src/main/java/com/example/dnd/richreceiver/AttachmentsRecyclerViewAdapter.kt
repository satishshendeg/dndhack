package com.example.dnd.richreceiver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dnd.R

internal class AttachmentsRecyclerViewAdapter(private val attachments: AttachmentsRepo) :
    RecyclerView.Adapter<AttachmentsRecyclerViewAdapter.ViewHolder>() {

    internal class ViewHolder(
        parent: ViewGroup,
        var attachmentThumbnailView: AppCompatImageView,
        var deleteAttachment: AppCompatImageButton
    ) : RecyclerView.ViewHolder(parent)

    override fun getItemCount(): Int {
        return attachments.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attachment, parent, false) as ViewGroup
        return ViewHolder(
            view,
            view.findViewById(R.id.attachment_thumbnail),
            view.findViewById(R.id.delete_attachment)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = attachments.allUris[position]
        holder.attachmentThumbnailView.setImageURI(uri)
        holder.deleteAttachment.setOnClickListener {
            attachments.delete(holder.adapterPosition)
            notifyItemRemoved(position)
        }
    }

}
