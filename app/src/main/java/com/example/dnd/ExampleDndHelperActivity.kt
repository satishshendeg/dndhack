package com.example.dnd

import android.content.ClipData
import android.content.ClipDescription
import android.content.res.Resources.getSystem
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.ContentInfoCompat
import androidx.core.view.DragStartHelper
import androidx.draganddrop.DropHelper
import com.bumptech.glide.Glide
import com.example.dnd.databinding.ActivityExampleDndhelperBinding

val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

class ExampleDndHelperActivity : AppCompatActivity() {
    val TAG = "ExampleDnDHelperActivity"
    val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityExampleDndhelperBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.greeting.text = "Drag and Drop using Androidx Helper classes"
        val sourceUrl =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbXQvdZOF0dsvBWOWxVqzfLdFz4yxVripPitHSuSchz7KXi1Ms7dN7Cw12GpOTvBLQfmE&usqp=CAU"
        Glide.with(this).load(sourceUrl).into(viewBinding.source)
        viewBinding.source.tag = sourceUrl
        setupDrag(viewBinding.source)
        setupDrop(viewBinding.target)
    }

    private fun setupDrag(draggableView: View) {
        DragStartHelper(draggableView)
        { view: View, _: DragStartHelper ->
            val item = ClipData.Item(view.tag as? CharSequence)
            val dragData = ClipData(
                view.tag as? CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            view.startDragAndDrop(
                dragData,
                View.DragShadowBuilder(view),
                null,
                0
            )
        }.attach()
    }

    private fun setupDrop(targetView: View) {
        DropHelper.configureView(
            this,
            targetView,
            arrayOf("text/*"),
            DropHelper.Options.Builder()
                .setHighlightColor(getColor(R.color.white))
                .setHighlightCornerRadiusPx(16.px)
                .build(),
        ) { _, payload: ContentInfoCompat ->
            // TODO: step through clips if one cannot be loaded
            val item = payload.clip.getItemAt(0)
            val dragData = item.text
            Glide.with(this)
                .load(dragData)
                .centerCrop().into(targetView as ImageView)
            // Consume payload by only returning remaining items
            val (_, remaining) = payload.partition { it == item }
            payload
        }
    }

}