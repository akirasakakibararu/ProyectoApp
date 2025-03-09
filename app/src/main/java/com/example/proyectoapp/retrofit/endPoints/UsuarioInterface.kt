package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.http.GET

interface UsuarioInterface {
    @GET("usuarios")
    fun getAllUsers(): Call<List<Usuario>>
}
