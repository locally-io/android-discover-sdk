package io.locally.discover.authentication

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {
    companion object {
        val instance: Retrofit by lazy {
            val httpClient = OkHttpClient.Builder().build()

            Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .baseUrl(BASE)
                    .build()
        }
    }
}