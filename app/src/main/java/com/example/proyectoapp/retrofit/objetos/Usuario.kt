package com.example.proyectoapp.retrofit.objetos

data class Usuario(
    val idUsuario: Int,
    var nombre: String,
    var email: String,
    var contrasena: String,
    var rol: String,
    var fotoPerfil: String,
    var habilitado: Boolean
) {

}