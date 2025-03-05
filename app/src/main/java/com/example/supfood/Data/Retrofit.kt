package com.example.supfood.data

import com.example.supfood.logic.Food2ForkApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://food2fork.ca/api/"

    val api: Food2ForkApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Food2ForkApi::class.java)
    }
}