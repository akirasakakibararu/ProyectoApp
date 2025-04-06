package com.example.proyectoapp.retrofit.instances

import android.content.Context
import android.content.Context.MODE_PRIVATE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object UserInterface {
    private const val BASE_URL = "http://10.0.2.2:8080"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun Context.getAuthToken(): String? {
        return getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getString("auth_token", null)
    }
}
