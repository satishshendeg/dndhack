package com.example.dnd

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ContentInfoCompat
import androidx.core.view.DragStartHelper
import androidx.draganddrop.DropHelper
import com.bumptech.glide.Glide
import com.example.dnd.databinding.ActivityExampleDndhelperBinding

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
        val targetUrl =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwxu7iDNuWhszXCgAyvGQlKCmRa41muXsym9ZDP7Mq9q9950X0gHHFfIdD2NM3etE4IN4&usqp=CAU"
        Glide.with(this).load(sourceUrl).into(viewBinding.source)
        Glide.with(this).load(targetUrl).into(viewBinding.target)
        viewBinding.source.tag = sourceUrl
        setupDrag(viewBinding.source)
        setupDrop(viewBinding.target)
    }

    private fun setupDrag(draggableView: View) {

        DragStartHelper(draggableView) { view: View, _: DragStartHelper ->
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
            DropHelper.Options.Builder().setHighlightColor(getColor(R.color.black))
                .setHighlightCornerRadiusPx(100).build(),
        ) { view, payload: ContentInfoCompat ->
            val item = payload.clip.getItemAt(0)
            val dragData = item.text
            Glide.with(this).load(dragData).into(targetView as ImageView)
            payload
        }
    }
}