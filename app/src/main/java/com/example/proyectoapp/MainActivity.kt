package com.example.proyectoapp

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.pojos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var users: List<Usuario>
    private val userApi: UsuarioInterface = UserInterface.retrofit.create(UsuarioInterface::class.java)
    private lateinit var animationDrawable: AnimationDrawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val icon = findViewById<ImageView>(R.id.icono)
        icon.setBackgroundResource(R.drawable.transition)

        animationDrawable = icon.background as AnimationDrawable
        animationDrawable.start()

        val animationDuration = 1000L

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, PantallaPrincipalActivity::class.java))
            finish()
        }, 5000)


    }


}
