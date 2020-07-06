package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    RecyclerViewAdapter adapter;
    Disposable disposable;
    SearchObservable searchObservable;
    RetrofitClientInstance.Endpoints retrofitEndpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchView searchView = findViewById(R.id.search_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        int numberOfColumns = 3;
        adapter = new RecyclerViewAdapter(this, new ArrayList<>());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        retrofitEndpoint = RetrofitClientInstance.getService();
        searchObservable = new SearchObservable();
        Observable<String> observable = searchObservable.getSearchObservableInstance(searchView);
        disposable = observable
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(text -> !text.isEmpty() && text.length() >= 2)
                .distinctUntilChanged()
                .flatMap(query -> Observable.create((ObservableOnSubscribe<ArrayList<Photo>>) emitter -> {
                    fetchPhotosFromQuery(query, emitter);
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photos -> adapter.update(photos), Throwable::printStackTrace);
    }

    private void fetchPhotosFromQuery(String query, @NonNull ObservableEmitter<ArrayList<Photo>> emitter) {
        Call<PhotoCollection> call = retrofitEndpoint.getAllPhotos(query);
        call.enqueue(new Callback<PhotoCollection>() {
            @Override
            public void onResponse(@androidx.annotation.NonNull Call<PhotoCollection> call, @androidx.annotation.NonNull Response<PhotoCollection> response) {
                if (response.isSuccessful())
                    emitter.onNext(Objects.requireNonNull(response.body()).getPhotoPage().getPhotoList());
            }

            @Override
            public void onFailure(@androidx.annotation.NonNull Call<PhotoCollection> call, @androidx.annotation.NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, FullImage.class);
        intent.putExtra("url", adapter.getUrlFromPosition(position));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        searchObservable.invalidate();
        disposable.dispose();
        super.onDestroy();
    }

    public static class SearchObservable {

        PublishSubject<String> subject = PublishSubject.create();

        public Observable<String> getSearchObservableInstance(SearchView searchView) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    subject.onNext(newText);
                    return false;
                }
            });

            return subject;
        }

        public void invalidate() {
            subject = null;
        }

    }

}