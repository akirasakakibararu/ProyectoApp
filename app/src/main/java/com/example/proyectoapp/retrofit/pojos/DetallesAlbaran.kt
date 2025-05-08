package com.example.proyectoapp.retrofit.pojos

import java.util.Date

data class DetallesAlbaran(
    val id: Int,
    val id_albaran: Int,
    val id_producto: Int,
    val cantidad: Int,
    val precio_unitario: Double,
    val importe: Double,
    val fecha: Date
){

}
