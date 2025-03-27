package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UsuarioInterface {
    @GET("usuarios")
    fun getAllUsers(): Call<List<Usuario>>

    @POST("auth/login")
    fun loginUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<String>
}

// Clase para el cuerpo de la solicitud de login
data class LoginRequest(
    val username: String,
    val password: String
)