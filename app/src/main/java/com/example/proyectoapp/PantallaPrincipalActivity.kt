package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PantallaPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener{
            val intent = Intent(this, PantallaProductosActivity::class.java)
            startActivity(intent)
        }
    }
}