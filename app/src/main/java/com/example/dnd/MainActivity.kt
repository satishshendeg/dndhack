package com.example.dnd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dnd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.btnViewImpl.setOnClickListener {
            startActivity(Intent(this, ExampleViewActivity::class.java))
        }

        viewBinding.btnDnDHelperImpl.setOnClickListener {
            startActivity(Intent(this, ExampleDndHelperActivity::class.java))
        }
    }
}