package com.example.dnd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dnd.databinding.ActivityDefaultdndBinding

class ExampleDefaultDnd : AppCompatActivity() {
    val TAG = "ExampleDefaultDnd"
    val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDefaultdndBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.greeting.text = "Drag and drop using custom default view"
        val sourceURL =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRxOHl5mi4qFgxafywhtLUIjdDsS7K40y5l-9rzvCXoXJZaFh0KUUkAU2YIloj7FZHT3Ts&usqp=CAU"
        val targetURL =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSBFc4q-73PrvNFtEv7KaSzmmKxNyHGGX2-c2m_GZnAmPHBUOZKvKuU22QOJk7FK0uS_s&usqp=CAU"
        Glide.with(this).load(sourceURL).into(viewBinding.source)
        viewBinding.source.tag = sourceURL
        Glide.with(this).load(targetURL).into(viewBinding.target)
        viewBinding.target.tag = targetURL
    }
}