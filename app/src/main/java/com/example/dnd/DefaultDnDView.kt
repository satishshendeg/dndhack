package com.example.dnd

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

class DefaultDnDView(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    val TAG = "DefaultDndView"
    var mEnableDrag = false
    var mEnableDrop = false

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.DefaultDnDView, 0, 0).apply {
            try {
                mEnableDrag = getBoolean(R.styleable.DefaultDnDView_enableDrag, false)
                mEnableDrop = getBoolean(R.styleable.DefaultDnDView_enableDrop, false)
                if (mEnableDrag) {
                    setupDrag()
                }
                if (mEnableDrop) {
                    setupDrop()
                }
            } finally {
                recycle()
            }
        }
    }

    private fun setupDrag() {

        setOnLongClickListener { v ->
            val item = ClipData.Item(v.tag as? CharSequence)
            val dragData = ClipData(
                v.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            v.startDragAndDrop(
                dragData,  // The data to be dragged.
                View.DragShadowBuilder(v),
                null,
                0
            )

            true
        }
    }

    private fun setupDrop() {
        setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(TAG, "ON DRAG STARTED")
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        (v as? ImageView)?.alpha = 0.5F
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d(TAG, "ON DRAG ENTERED")
                    (v as? ImageView)?.alpha = 0.3F
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    Log.d(TAG, "On DRAG LOCATION")
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d(TAG, "ON DRAG ENDED")
                    (v as? ImageView)?.alpha = 1.0F
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.d(TAG, "ON DRAG EXISITED")
                    (v as? ImageView)?.alpha = 0.5F
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    Log.d(TAG, "On DROP")
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    Glide.with(this).load(item.text).into(v as ImageView)
                    (v as? ImageView)?.alpha = 1.0F
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

}