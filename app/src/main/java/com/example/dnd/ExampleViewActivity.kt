package com.example.dnd

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dnd.databinding.ActivityExampleViewBinding

class ExampleViewActivity : AppCompatActivity() {
    val TAG = "ExampleViewActivity"
    val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityExampleViewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.greeting.text = "View Drag and Drop Example"
        val sourceUrl =
            "https://www.chemistry.msu.edu/_assets/_images/portraits/Albert%20Einstein.jpg"
        val targetUrl =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTuvd50QokY0nAm4J8Tbc4bBFKQwRWahlu57XUkfSrdiVsJpDNlaOQKN-Lquef9p-mATr0&usqp=CAU"
        viewBinding.source.tag = sourceUrl
        Glide.with(this).load(sourceUrl).into(viewBinding.source)
        Glide.with(this).load(targetUrl).into(viewBinding.target)
        setupDrag(viewBinding.source)
        setupDrop(viewBinding.target)
    }

    fun setupDrag(draggableView: View) {
        draggableView.setOnLongClickListener { v ->
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


    fun setupDrop(dropView: View) {
        dropView.setOnDragListener { v, event ->
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
                    Toast.makeText(this, "Dragged Data ${dragData}", Toast.LENGTH_SHORT).show()
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