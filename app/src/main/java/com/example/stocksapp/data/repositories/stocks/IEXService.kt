package com.example.stocksapp.data.repositories.stocks

import com.example.stocksapp.BuildConfig
import com.example.stocksapp.data.model.network.CompanyInfoResponse
import com.example.stocksapp.data.model.network.NewsResponse
import com.example.stocksapp.data.model.network.PriceResponse
import com.example.stocksapp.data.model.network.QuoteResponse
import com.example.stocksapp.data.model.network.SymbolResponse
import com.example.stocksapp.data.model.network.SymbolType
import com.example.stocksapp.data.repositories.utils.BatchedNews
import com.example.stocksapp.data.repositories.utils.BatchedNewsAdapter
import com.example.stocksapp.data.repositories.utils.BatchedQuotes
import com.example.stocksapp.data.repositories.utils.BatchedQuotesAdapter
import com.example.stocksapp.data.repositories.utils.HttpRequestInterceptor
import com.example.stocksapp.data.repositories.utils.InstantAdapter
import com.example.stocksapp.data.repositories.utils.LocalDateAdapter
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IEXService {

    @GET("ref-data/symbols")
    suspend fun fetchSymbols(): ApiResponse<List<SymbolResponse>>

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
    suspend fun fetchChartPrices(
        @Path("symbol") symbol: String,
        @Path("range") range: ChartRanges,
        @Query("includeToday") includeToday: Boolean = false,
        @Query("chartCloseOnly") closeOnly: Boolean = true
    ): ApiResponse<List<PriceResponse>>

    @GET("stock/market/batch?types=news")
    @BatchedNews
    suspend fun fetchNews(
        @Query("symbols") symbols: String,
        @Query("last") numberPerSymbol: Int = 2
    ): ApiResponse<List<NewsResponse>>

    @GET("stock/{symbol}/company")
    suspend fun fetchCompanyInfo(
        @Path("symbol") symbol: String
    ): ApiResponse<CompanyInfoResponse>

    companion object {
        // private const val BASE_URL = "https://cloud.iexapis.com/stable/"
        private const val BASE_URL = "https://sandbox.iexapis.com/stable/"

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
                            .add(
                                SymbolType::class.java,
                                EnumJsonAdapter.create(SymbolType::class.java).withUnknownFallback(SymbolType.Unknown)
                            )
                            .add(BatchedQuotesAdapter())
                            .add(BatchedNewsAdapter())
                            .add(LocalDateAdapter())
                            .add(InstantAdapter())
                            .build()
                    )
                )
                .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
                .build()
                .create(IEXService::class.java)
        }
    }

    enum class ChartRanges(private val urlString: String) {
        OneWeek("7d"),
        OneMonth("1m"),
        ThreeMonths("3m"),
        OneYear("1y");
        // All("max"); // TODO: Add "All" range support

        override fun toString(): String {
            return urlString
        }
    }
}
