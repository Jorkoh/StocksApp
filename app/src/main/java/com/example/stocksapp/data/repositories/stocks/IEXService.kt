package com.example.stocksapp.data.repositories.stocks

import com.example.stocksapp.BuildConfig
import com.example.stocksapp.data.model.network.CompanyInfoResponse
import com.example.stocksapp.data.model.network.QuoteResponse
import com.example.stocksapp.data.repositories.utils.BatchedQuotes
import com.example.stocksapp.data.repositories.utils.BatchedQuotesAdapter
import com.example.stocksapp.data.repositories.utils.HttpRequestInterceptor
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IEXService {

    @GET("stock/market/list/mostactive")
    suspend fun fetchMostActiveSymbols(
        @Query("listLimit") numberReturned: Int = 20
    ): ApiResponse<List<QuoteResponse>>

    @GET("stock/market/batch?types=quote")
    @BatchedQuotes
    suspend fun fetchQuotes(
        @Query("symbols") symbols: String
    ): ApiResponse<List<QuoteResponse>>

    @GET("stock/{symbol}/chart/{range}")
    suspend fun fetchChart(
        @Query("symbol") symbol: String,
        @Query("range") range: ChartRanges
    )

    @GET("stock/{symbol}/company")
    suspend fun fetchCompanyInfo(
        @Path("symbol") symbol: String
    ): ApiResponse<CompanyInfoResponse>

    companion object {
        private const val BASE_URL = "https://cloud.iexapis.com/stable/"

        fun create(): IEXService {
            return Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(
                            Interceptor { chain ->
                                // Add the API token to the request
                                val originalRequest = chain.request()
                                val urlWithToken = originalRequest.url.newBuilder()
                                    .addQueryParameter("token", BuildConfig.IEX_PUBLISHABLE_TOKEN)
                                    .build()
                                val requestWithToken = originalRequest.newBuilder()
                                    .url(urlWithToken)
                                    .build()
                                chain.proceed(requestWithToken)
                            }
                        )
                        .addInterceptor(HttpRequestInterceptor())
                        .build()
                )
                .baseUrl(BASE_URL)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .add(BatchedQuotesAdapter())
                            .build()
                    )
                )
                .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
                .build()
                .create(IEXService::class.java)
        }
    }
}
