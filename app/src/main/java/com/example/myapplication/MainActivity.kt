package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RetrofitClientInstance.Endpoints
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.ItemClickListener {
    private var adapterRecycler: RecyclerViewAdapter? = null
    private var disposable: Disposable? = null
    private var searchObservable: SearchObservable = SearchObservable()
    private var retrofitEndpoint: Endpoints? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchView = findViewById<SearchView>(R.id.search_view)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val numberOfColumns = 3
        adapterRecycler = RecyclerViewAdapter(this, ArrayList())
        adapterRecycler?.setClickListener(this)
        recyclerView.adapter = adapterRecycler
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        retrofitEndpoint = RetrofitClientInstance.service
        val observable = searchObservable.getSearchObservableInstance(searchView)
        disposable = observable?.debounce(500, TimeUnit.MILLISECONDS)?.filter { text: String -> text.isNotEmpty() && text.length >= 2 }?.distinctUntilChanged()?.flatMap { query: String -> Observable.create(ObservableOnSubscribe { emitter: ObservableEmitter<ArrayList<Photo>>? -> fetchPhotosFromQuery(query, emitter) } as ObservableOnSubscribe<ArrayList<Photo>>) }?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ photos: ArrayList<Photo> -> adapterRecycler?.update(photos) }, { obj: Throwable -> obj.printStackTrace() })
    }

    private fun fetchPhotosFromQuery(query: String, emitter: @NonNull ObservableEmitter<ArrayList<Photo>>?) {
        val call = retrofitEndpoint?.getAllPhotos(query)
        call?.enqueue(object : Callback<PhotoCollection?> {
            override fun onResponse(call: Call<PhotoCollection?>, response: Response<PhotoCollection?>) {
                if (response.isSuccessful) emitter?.onNext(response.body()?.photoPage?.photoList)
            }

            override fun onFailure(call: Call<PhotoCollection?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(view: View?, position: Int) {
        val intent = Intent(this, FullImage::class.java)
        intent.putExtra("url", adapterRecycler?.getUrlFromPosition(position))
        startActivity(intent)
    }

    override fun onDestroy() {
        searchObservable.invalidate()
        disposable?.dispose()
        super.onDestroy()
    }

    class SearchObservable {
        var subject: PublishSubject<String>? = PublishSubject.create<String>()
        fun getSearchObservableInstance(searchView: SearchView): Observable<String>? {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    subject?.onNext(newText)
                    return false
                }
            })
            return subject
        }

        fun invalidate() {
            subject = null
        }
    }
}