package com.example.stocksapp.data.repositories.stocks

import com.example.stocksapp.data.repositories.utils.HttpRequestInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface IEXService {

    companion object {
        private const val BASE_URL = "https://cloud.iexapis.com/"

        fun create(): IEXService {
            return Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpRequestInterceptor())
                        .build()
                )
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(IEXService::class.java)
        }
    }
}