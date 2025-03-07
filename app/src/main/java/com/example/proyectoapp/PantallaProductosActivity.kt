package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PantallaProductosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_productos)

        val imageButton = findViewById<ImageButton>(R.id.imageButton6)

        imageButton.setOnClickListener{
            val intent = Intent(this, AnnadirProductoActivity::class.java)
            startActivity(intent)
        }
    }
}