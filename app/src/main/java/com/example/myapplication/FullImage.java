package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class FullImage extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        ImageView imageView = findViewById(R.id.full_image_view);

        String url = Objects.requireNonNull(getIntent().getExtras()).getString("url");

        Glide.with(this).load(url).into(imageView);
    }

}