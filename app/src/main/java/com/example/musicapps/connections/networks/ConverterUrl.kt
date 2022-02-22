package com.example.musicapps.connections.networks

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ConverterUrl {
    private var retrofit: Retrofit? = null
    private var BASE_URL = "https://api.musixmatch.com/"

    val client: Retrofit?
        get() {
            if (retrofit == null) {
                val okHttpClient = OkHttpClient.Builder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .build()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }

            return retrofit
        }
}