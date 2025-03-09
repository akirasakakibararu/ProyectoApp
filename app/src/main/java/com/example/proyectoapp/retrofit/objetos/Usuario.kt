package com.example.proyectoapp.retrofit.objetos

data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val role: String,
    val picture: String,
    val habilitado: Boolean
) {

}