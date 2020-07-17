package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class FullImage : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_image)
        val imageView = findViewById<ImageView>(R.id.full_image_view)
        val url = intent.extras?.getString("url")
        Glide.with(this).load(url).into(imageView)
    }
}