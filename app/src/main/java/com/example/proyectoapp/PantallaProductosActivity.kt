package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoapp.retrofit.adapter.UserAdapter
import com.example.proyectoapp.retrofit.endPoints.LoginRequest
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantallaProductosActivity : AppCompatActivity() {

    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)

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
        loginUser(nombre.toString(), contrasena.toString())
        imageButton.setOnClickListener{

            val intent = Intent(this, AnnadirProductoActivity::class.java)
            startActivity(intent)
        }
    }
    private fun loginUser(username: String, password: String) {
        Log.e("nombre:", username)
        Log.e("contrasena:", password)
        val call = userApi.loginUser(username, password)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    Log.e("Response err:", response.code().toString())
                    Log.e("Response err:", response.message())
                    return
                }
                response.body()?.let {
                    Log.i("token:", it)
                    // Guarda el token en SharedPreferences
                    getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                        .putString("auth_token", it)
                        .apply()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }

}