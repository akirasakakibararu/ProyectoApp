package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Productos
import retrofit2.Call
import retrofit2.http.*

interface ProductoInterface {

    @GET("api/productos")
    fun getAllProductos(
        @Header("Authorization") token: String
    ): Call<List<Productos>>

    @POST("api/productos")
    fun añadirProducto(
        @Header("Authorization") token: String,
        @Body product: Productos
    ): Call<Productos>

    @PUT("api/productos/{id}")
    fun editarProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body product: Productos
    ): Call<Productos>

    @DELETE("api/productos/{id}")
    fun eliminarProducto(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>

    @POST("api/productos/{id}/aumentarStock")
    fun aumentarStock(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ):  Call<Void>

    @POST("api/productos/{id}/disminuirStock")
    fun disminuirStock(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ):  Call<Void>
}
