package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.pojos.Albaran
import com.example.proyectoapp.retrofit.pojos.Movimientos
import com.example.proyectoapp.retrofit.pojos.Productos
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MovimientosInterface {


    @GET("api/movimientos")
    fun getAllAlbaranes(
        @Header("Authorization") token: String
    ): Call<List<Movimientos>>

    @POST("api/movimientos")
    fun añadirMovimiento(
        @Header("Authorization") token: String,
        @Body movimiento: Movimientos
    ): Call<Movimientos>

    @GET("api/movimientos/{id}")
    fun getbyUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<List<Movimientos>>
}