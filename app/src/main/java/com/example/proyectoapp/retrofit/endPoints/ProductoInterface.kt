package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.pojos.Productos
import retrofit2.Call
import retrofit2.Response
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


    @GET("api/productos/inventario")
    fun getProductos(
        @Header("Authorization") token: String,
        @Query("filtro") filtro: String? = null
    ): Call<List<Productos>>
    @FormUrlEncoded
    @POST("api/productos/inventario/enviar")
    fun enviarInventario(
        @Header("Authorization") token: String,
        @Field("email") email: String,
        @Field("filtro") filtro: String? = null
    ): Call<Void>
}
