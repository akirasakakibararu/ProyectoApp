package com.example.proyectoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoapp.retrofit.adapter.UserAdapter
import com.example.proyectoapp.retrofit.endPoints.UsuarioInterface
import com.example.proyectoapp.retrofit.instances.UserInterface
import com.example.proyectoapp.retrofit.objetos.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantallaPrincipalActivity : AppCompatActivity() {
    private lateinit var users: List<Usuario>
    private lateinit var listado: ListView
    private val userApi: UsuarioInterface =
        UserInterface.retrofit.create(UsuarioInterface::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)
        listado = findViewById(R.id.listadeusuarios)
        getAllUsers()
        listado.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedUser = users[position]

            // Crear Intent correctamente
            val intent = Intent(this@PantallaPrincipalActivity, PantallaProductosActivity::class.java)

            // Pasar datos al Intent
            intent.putExtra("userId", selectedUser.idUsuario)
            intent.putExtra("nombre", selectedUser.nombre)
            intent.putExtra("email", selectedUser.email)
            intent.putExtra("contrasena", selectedUser.contrasena)
            intent.putExtra("rol", selectedUser.rol)
            intent.putExtra("fotoPerfil", selectedUser.fotoPerfil)
            intent.putExtra("habilitado", selectedUser.habilitado)

            // Iniciar la nueva actividad
            startActivity(intent)
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

                val myadapter = UserAdapter(this@PantallaPrincipalActivity, users)
                listado.adapter = myadapter

                //    val intent = Intent(this@PantallaPrincipalActivity, PantallaProductosActivity::class.java)

                //  startActivity(intent)
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("Error:", t.message ?: "Error desconocido")
            }
        })
    }
}