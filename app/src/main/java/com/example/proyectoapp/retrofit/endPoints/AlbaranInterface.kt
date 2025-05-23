package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.pojos.Albaran
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlbaranInterface {

    @GET("api/albaranes")
    fun getAllAlbaranes(
        @Header("Authorization") token: String
    ): Call<List<Albaran>>

    @POST("api/albaranes")
    fun anadirAlbaran(
        @Header("Authorization") token: String,
        @Body albaran: Albaran
    ): Call<Albaran>

    @PUT("api/albaranes/{id}")
    fun editarAlbaran(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body albaran: Albaran
    ): Call<Albaran>

    @DELETE("api/albaranes/{id}")
    fun eliminarProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}