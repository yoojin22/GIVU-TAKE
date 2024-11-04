package com.project.givuandtake.core.apis.Gift

import com.project.givuandtake.core.data.Gift.GiftReviewData
import com.project.givuandtake.core.data.MainPage.TotalGivuData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GiftReviewApiService {
    @GET("gifts/{giftIdx}/review")
    suspend fun getGiftReviewData(
        @Path("giftIdx") giftIdx: Int,
        @Query("pageNo") pageNo: Int?,
        @Query("pageSize") pageSize: Int = 50,
        @Query("isOrderLiked") isOrderLiked: Boolean = true
    ): Response<GiftReviewData>
}

object GiftReviewApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: GiftReviewApiService by lazy {
        // HttpLoggingInterceptor 추가
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // OkHttpClient 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiftReviewApiService::class.java)
    }
}