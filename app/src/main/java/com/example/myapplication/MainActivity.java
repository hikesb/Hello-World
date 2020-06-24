package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Call<PhotoCollection> call = RetrofitClientInstance.getService().getAllPhotos();
        call.enqueue(new Callback<PhotoCollection>() {
            @Override
            public void onResponse(@NonNull Call<PhotoCollection> call, @NonNull Response<PhotoCollection> response) {
                if (response.isSuccessful())
                    generateDataList(Objects.requireNonNull(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<PhotoCollection> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(PhotoCollection photoCollection) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 3;
        adapter = new RecyclerViewAdapter(this, photoCollection.getPhotoPage().getPhoto());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, FullImage.class);
        intent.putExtra("url", adapter.getUrlFromPosition(position));
        startActivity(intent);
    }
}