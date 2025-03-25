package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var users: List<Usuario>
    private val userApi: UsuarioInterface = UserInterface.retrofit.create(UsuarioInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            getAllUsers()
            //val intent = Intent(this, PantallaPrincipalActivity::class.java)
            //startActivity(intent)
        }

    }

    private fun getAllUsers() {
        val call = userApi.getAllUsers()
        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.message())
                    return
                }
                users = response.body() ?: emptyList()
                users.forEach { Log.i("Usuarios:", it.toString()) }

                val intent = Intent(this@MainActivity, PantallaPrincipalActivity::class.java)

                startActivity(intent)
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
}
