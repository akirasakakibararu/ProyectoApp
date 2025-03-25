package com.example.proyectoapp.retrofit.objetos

data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val rol: String,
    val fotoPerfil: String,
    val habilitado: Boolean
) {

}