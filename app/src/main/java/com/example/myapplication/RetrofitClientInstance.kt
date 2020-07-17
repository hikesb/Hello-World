package com.example.myapplication

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object RetrofitClientInstance {
    private const val BASE_URL = "https://api.flickr.com/services/rest/"
    private var retrofit: Retrofit? = null
    private val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()

    private val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(Gson()))
                        .build()
            }
            return retrofit
        }

    val service: Endpoints?
        get() = retrofitInstance?.create(Endpoints::class.java)

    interface Endpoints {
        @GET("?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1")
        fun getAllPhotos(@Query("text") text: String?): Call<PhotoCollection?>?
    }
}