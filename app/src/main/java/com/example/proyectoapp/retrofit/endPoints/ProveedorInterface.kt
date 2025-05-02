package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Albaran
import com.example.proyectoapp.retrofit.objetos.Proveedores
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ProveedorInterface {
    @GET("api/proveedores")
    fun getAllProveedores(
        @Header("Authorization") token: String
    ): Call<List<Proveedores>>
    @POST("api/proveedores")
    fun anadirProveedor(
        @Header("Authorization") token: String,
        @Body proveedor: Proveedores
    ): Call<Proveedores>
}