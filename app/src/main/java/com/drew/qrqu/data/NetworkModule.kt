package com.drew.qrqu.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    // Kita gunakan dummy API untuk testing upload
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val apiService: QrApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QrApiService::class.java)
    }
}
