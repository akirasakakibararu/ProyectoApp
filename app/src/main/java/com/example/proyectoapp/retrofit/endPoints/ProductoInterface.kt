package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Productos
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductoInterface {
    @GET("productos")
    fun getAllProductos(@Header("Authorization") token: String): Call<List<Productos>>

    @POST("productos/actualizar")
    fun actualizarProductos(
        @Header("Authorization") token: String,
        @Body product: List<Productos>
    ): Call<String>
}