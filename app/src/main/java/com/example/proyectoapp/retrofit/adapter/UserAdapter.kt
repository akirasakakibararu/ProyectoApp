package com.example.proyectoapp.retrofit.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.objetos.Usuario
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat

class UserAdapter(private val mContext: Context, private val listaUsuarios: List<Usuario>) :
    ArrayAdapter<Usuario>(mContext, 0, listaUsuarios) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.usuariolista, parent, false)
        val usuario = listaUsuarios[position]

        val nameTextView = layout.findViewById<TextView>(R.id.nombreUsuario)
        val userImage = layout.findViewById<ImageView>(R.id.imageUser)

        // Asignar nombre
        nameTextView.text = usuario.nombre ?: "Sin nombre"

        // Manejo seguro de la imagen
        val defaultImage = ContextCompat.getDrawable(mContext, R.drawable.perfil_estandar)
        try {
            if (!usuario.fotoPerfil.isNullOrEmpty()) {
                val bitmap = base64ToBitmap(usuario.fotoPerfil)
                if (bitmap != null) {
                    userImage.setImageBitmap(bitmap)
                    Log.e("UserAdapter", "imagencorrecta:")
                } else {
                    userImage.setImageDrawable(defaultImage)
                    Log.e("UserAdapter", "imagenIncorrecta:")
                }
            } else {
                userImage.setImageDrawable(defaultImage)
            }
        } catch (e: Exception) {
            Log.e("UserAdapter", "Error al cargar imagen: ${e.message}")
            userImage.setImageDrawable(defaultImage)
        }

        return layout
    }

    private fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            Log.e("UserAdapter", "convertido:")
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        } catch (e: Exception) {
            Log.e("Base64Conversion", "Error al convertir Base64: ${e.message}")
            null
        }
    }
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}