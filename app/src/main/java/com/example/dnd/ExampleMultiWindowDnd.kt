package com.example.dnd;

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide
import com.example.dnd.databinding.ActivityMultiWindowdndBinding

public class ExampleMultiWindowDnd : AppCompatActivity() {
    val TAG = "ExampleMultiWindowDnd"

    val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMultiWindowdndBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.greeting.text = "Drag and Drop across apps in multi window mode"
        val sourceURL =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTyw_Hv-lueVWxnQJvvOwtKqT2oju-PLkgN4yC71apWCXH5HdwTJVx7GQbewOw0nfx5oI&usqp=CAU"
        val targetURL =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSwDAagO-dJP0fBkogvCqU9VfaimTDEQFB3kjXp5U1IGeE0EPN7ZWYCtjcOPdOsuf-VJGc&usqp=CAU"
        viewBinding.source.tag = sourceURL
        Glide.with(this).load(sourceURL).into(viewBinding.source)
        Glide.with(this).load(targetURL).into(viewBinding.target)
        setupDrag(viewBinding.source)
        setupDrop(viewBinding.target)
    }

    private fun setupDrag(draggableView: View) {
        draggableView.setOnLongClickListener { v ->
            v.startDragAndDrop(
                ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    ClipData.Item(v.tag as? CharSequence)
                ),
                View.DragShadowBuilder(v),
                null,
                View.DRAG_FLAG_GLOBAL or View.DRAG_FLAG_GLOBAL_URI_READ
            )
        }
    }

    private fun setupDrop(droppedView: View) {
        droppedView.setOnDragListener { view, event ->

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.d(TAG, "ON DRAG STARTED")
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        (view as? ImageView)?.alpha = 0.5F
                        view.invalidate()
                        true
                    } else {
                        false
                    }
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    Log.d(TAG, "ON DRAG ENTERED")
                    (view as? ImageView)?.alpha = 0.3F
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    Log.d(TAG, "On DRAG LOCATION")
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.d(TAG, "ON DRAG ENDED")
                    (view as? ImageView)?.alpha = 1.0F
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.d(TAG, "ON DRAG EXISITED")
                    (view as? ImageView)?.alpha = 0.5F
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    Log.d(TAG, "setupDrop: Action Dropped")
                    val dropPermission = requestDragAndDropPermissions(event)
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    Toast.makeText(this, "Dragged Data ${dragData}", Toast.LENGTH_SHORT).show()
                    Glide.with(this).load(item.text).into(view as ImageView)
                    (view as? ImageView)?.alpha = 1.0F
                    dropPermission.release()
                    true
                }

                else -> true
            }
        }
    }
}
