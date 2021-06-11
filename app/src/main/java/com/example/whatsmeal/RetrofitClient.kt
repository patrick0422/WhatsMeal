package com.example.whatsmeal

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    fun getService() : Retrofit = Retrofit.Builder()
        .baseUrl("https://open.neis.go.kr/hub/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}