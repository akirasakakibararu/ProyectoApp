package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.pojos.DetallesAlbaran
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DetalleAlbaranInterfaz {

    // Insertar lote de detalles
    @POST("api/detalles/lote")
    fun insertarLoteDetalles(
        @Header("Authorization") token: String,
        @Body detalles: List<DetallesAlbaran>
    ): Call<String>

    // Obtener detalles de un albarán por su id
    @GET("api/detalles/albaran/{id}")
    fun getDetallesPorAlbaran(
        @Header("Authorization") token: String,
        @Path("id") idAlbaran: Int
    ): Call<List<DetallesAlbaran>>
}
