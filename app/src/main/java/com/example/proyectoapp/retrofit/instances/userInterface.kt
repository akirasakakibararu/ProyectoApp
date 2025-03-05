package com.example.proyectoapp.retrofit.instances

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class userInterface {
    var retrofit: Retrofit? = null

    val BASE_URL: String = "http://10.0.2.2:8080"

    fun getRetrofitInstance(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}