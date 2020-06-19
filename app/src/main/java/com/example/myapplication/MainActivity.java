package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                Endpoints service = RetrofitClientInstance.getRetrofitInstance().create(Endpoints.class);
                Call<Photo_Base> call = service.getAllPhotos();
                call.enqueue(new Callback<Photo_Base>() {
                    @Override
                    public void onResponse(Call<Photo_Base> call, Response<Photo_Base> response) {
                        Log.println(Log.DEBUG,"Code", String.valueOf(response.code()));
                        generateDataList(response.body());
                    }

                    @Override
                    public void onFailure(Call<Photo_Base> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        thread.start();
    }

    private void generateDataList(Photo_Base photo_base) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 3;
        adapter = new RecyclerViewAdapter(this, new ArrayList<>(photo_base.getPhotos().getPhoto()));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent=new Intent(this,FullImage.class);
        intent.putExtra("id",R.drawable.pic1);
        startActivity(intent);
    }
}