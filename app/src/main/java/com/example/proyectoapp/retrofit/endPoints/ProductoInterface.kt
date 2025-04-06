package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Productos
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ProductoInterface {
    @GET("productos")
    fun getAllProductos(@Header("Authorization") token: String): Call<List<Productos>>

}