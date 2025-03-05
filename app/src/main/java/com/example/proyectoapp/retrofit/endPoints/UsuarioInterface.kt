package com.example.proyectoapp.retrofit.endPoints

import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.http.GET



interface UsuarioInterface {
    @GET("/usuario/getAll")
    fun getAllUsers(): Call<List<Usuario?>?>?

}