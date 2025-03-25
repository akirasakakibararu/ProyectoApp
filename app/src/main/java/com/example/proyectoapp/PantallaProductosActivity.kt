package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoapp.retrofit.objetos.Usuario

class PantallaProductosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_productos)

        val imageButton = findViewById<ImageButton>(R.id.imageButton6)
        val datos = this.intent.extras
        val nombre = datos?.getString("nombre")
        val email = datos?.getString("email")
        val contrasena = datos?.getString("contrasena")
        val rol = datos?.getString("rol")
        val foto = datos?.getString("foto")
        val id = datos?.getInt("userId")
        Log.e("nombre:", nombre.toString())
        Log.e("email:", email.toString())
        Log.e("contrasena:", contrasena.toString())
        Log.e("rol:", rol.toString())
        Log.e("foto:", foto.toString())
        Log.e("id:", id.toString())
        imageButton.setOnClickListener{
            val intent = Intent(this, AnnadirProductoActivity::class.java)
            startActivity(intent)
        }
    }
}