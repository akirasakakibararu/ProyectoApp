package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Productos
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioInterface {
    @GET("usuarios")
    fun getAllUsers(): Call<List<Usuario>>

    @POST("auth/login")
    fun loginUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<String>

    @POST("usuarios")
    fun registerUser(
        @Header("Authorization") token: String,
        @Body usuario: Usuario,
    ): Call<Usuario>
    @PUT("usuarios/{id}")
    fun editarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body usuario: Usuario
    ): Call<Usuario>
    @DELETE("usuarios/{id}")
    fun eliminarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}