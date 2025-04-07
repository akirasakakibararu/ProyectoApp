package com.example.proyectoapp.retrofit.objetos

data class Productos(
    val idProducto: Int,
    val nombre: String,
    val precioUnitario: Double,
    val descripcion: String,
    val foto: String,
    var stockActual: Int,
    val stockMinimo: Int,
    val proveedor: Int
) {
}