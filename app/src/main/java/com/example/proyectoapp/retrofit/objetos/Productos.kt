package com.example.proyectoapp.retrofit.objetos

data class Productos(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val picture: String,
    val stock: Int,
    val stock_min: Int,
    val habilitado: Boolean,
    val id_proveedor: Int
) {
}