package com.example.proyectoapp.retrofit.pojos

import java.math.BigDecimal
import java.util.Date
 data class DetallesAlbaran(
    val idDetalle: Int? = null,
    val producto: Int,
    val albaran: Int,
    val precioUnitario: BigDecimal,
    val cantidad: Int
){

}
