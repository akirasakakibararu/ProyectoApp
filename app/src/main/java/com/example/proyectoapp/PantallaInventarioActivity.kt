package com.example.proyectoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.objetos.Usuario

class PantallaInventarioActivity : AppCompatActivity() {
    private lateinit var users: List<Usuario>

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)
    private lateinit var gridLayout: GridLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usuariolista)
        gridLayout = findViewById(R.id.idGridLayout)



    }
}