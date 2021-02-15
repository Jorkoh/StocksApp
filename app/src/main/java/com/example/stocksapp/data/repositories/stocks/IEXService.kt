package com.example.stocksapp.data.repositories.stocks

import com.example.stocksapp.BuildConfig
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.repositories.utils.HttpRequestInterceptor
import com.skydoves.sandwich.ApiResponse
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


interface IEXService {

    @GET("stock/{symbol}/company")
    suspend fun fetchCompanyInfo(
        @Path("symbol") symbol: String
    ): ApiResponse<CompanyInfo>

    companion object {
        private const val BASE_URL = "https://cloud.iexapis.com/"

        fun create(): IEXService = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpRequestInterceptor())
                    .addInterceptor(Interceptor { chain ->
                        // Add the API token to the request
                        val originalRequest = chain.request()
                        val urlWithToken = originalRequest.url.newBuilder()
                            .addQueryParameter("token", BuildConfig.IEX_PUBLISHABLE_TOKEN)
                            .build()
                        val requestWithToken = originalRequest.newBuilder()
                            .url(urlWithToken)
                            .build()
                        chain.proceed(requestWithToken)
                    })
                    .build()
            )
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(IEXService::class.java)
    }
}