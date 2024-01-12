package com.example.dnd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class ExampleComposeActivity : ComponentActivity() {
    val TAG = "ExampleComposeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DnDComposable()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DnDComposable() {
    val sourceUrl =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTiSHrXAV6vKmoUA2aGHbJIyGux3qjoUSp0gOghJMFczgb-1TeyRCWXQnx-IyHB0xu_4l0&usqp=CAU"
    val targetUrl =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSLdHZcdUhiWHkwNeAhopQQ3KA0homrp6bY3w4bwV_Rv7YMtBkJ57hRJgHRGQkLigxJ248&usqp=CAU"
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Drag and Drop Example Using compose")
        GlideImage(
            modifier = Modifier
                .width(240.dp)
                .height(240.dp),
            model = sourceUrl,
            contentDescription = "Source View"
        )
        GlideImage(
            modifier = Modifier
                .width(240.dp)
                .height(240.dp),
            model = targetUrl,
            contentDescription = "Target ImageView"
        )

    }
}