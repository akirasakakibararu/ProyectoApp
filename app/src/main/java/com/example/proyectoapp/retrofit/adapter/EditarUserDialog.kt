package com.example.proyectoapp.retrofit.adapter

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.pojos.Usuario
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditarUserDialog(
    var usuario: Usuario,
    val onUsuarioEditado: (Usuario) -> Unit,
    val onUsuarioEliminado: (Int) -> Unit
) : DialogFragment() {

    private lateinit var imageButtonLogo: ImageButton
    private var imagenCambiada = false
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.editarusuario, null)
        val nombre = view.findViewById<EditText>(R.id.editNombreUser)
        val password = view.findViewById<EditText>(R.id.editPassword)
        val email = view.findViewById<EditText>(R.id.editEmail)

        val boton = view.findViewById<Button>(R.id.btnEditar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        imageButtonLogo = view.findViewById(R.id.botonFoto2)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        val rol = view.findViewById<CheckBox>(R.id.checkBoxRolEdit)
        if (usuario.rol == "Administrador") {
            rol.isChecked = true
            rol.setText("Administrador")
        } else {
            rol.isChecked = false
            rol.setText("Empleado")
        }
        rol.setOnCheckedChangeListener { _, isChecked ->
            rol.setText(if (isChecked) "Administrador" else "Empleado")
        }

        val habilitado = view.findViewById<CheckBox>(R.id.checkBoxHabilitar)
        if(usuario.habilitado){
            habilitado.isChecked = true
            habilitado.setText("Habilitado")
        }else{
            habilitado.isChecked = false
            habilitado.setText("Deshabilitado")
        }

        habilitado.setOnCheckedChangeListener { _, isChecked ->
            usuario.habilitado = isChecked
            habilitado.setText(if (isChecked) "Habilitado" else "Deshabilitado")
        }


        nombre.setText(usuario.nombre)
        password.setText(usuario.contrasena)
        email.setText(usuario.email)


        cargarImagenDesdeString(usuario.fotoPerfil, imageButtonLogo)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        boton.setOnClickListener {
            val bitmap = (imageButtonLogo.drawable as BitmapDrawable).bitmap
            val fotoBase64 = bitmapToBase64(bitmap)

            if (nombre.text.toString() == "" || password.text.toString() == "" || email.text.toString() == "") {
                Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (imagenCambiada) {
                    val usuarioEditado = Usuario(
                        usuario.idUsuario,
                        nombre.text.toString(),
                        email.text.toString(),
                        password.text.toString(),
                        rol.text.toString(),
                        fotoBase64,
                        usuario.habilitado
                    )
                    onUsuarioEditado(usuarioEditado)
                } else {
                    val usuarioEditado = Usuario(
                        usuario.idUsuario,
                        nombre.text.toString(),
                        email.text.toString(),
                        password.text.toString(),
                        rol.text.toString(),
                        usuario.fotoPerfil,
                        usuario.habilitado
                    )
                    onUsuarioEditado(usuarioEditado)
                }
            }



            dialog.dismiss()
        }

        btnEliminar.setOnClickListener {
            onUsuarioEliminado(usuario.idUsuario)
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        imageButtonLogo.setOnClickListener {
            imagenCambiada = true
            takePicture()
        }

        return dialog
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {//Función para recibir la foto
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            val imgBitmap = extras?.get("data") as Bitmap
            imageButtonLogo.setImageBitmap(imgBitmap)
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return "data:image/png;base64,$base64"
    }

    fun identificarImagen(input: String): String {
        return when {
            input.startsWith(
                "data:image",
                ignoreCase = true
            ) && input.contains("base64,") -> "base64"

            input.startsWith("http", ignoreCase = true) -> "url"
            else -> "desconocido"
        }
    }

    fun cargarImagenDesdeString(input: String, imageboton: ImageButton) {
        when (identificarImagen(input)) {
            "url" -> {
                Picasso.get().load(input).into(imageboton)
            }

            "base64" -> {
                try {
                    val base64Data = input.substringAfter("base64,")
                    val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imageboton.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    imageboton.setImageResource(R.drawable.perfil_estandar)
                }
            }

            else -> {
                // Imagen desconocida
                imageboton.setImageResource(R.drawable.perfil_estandar)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun takePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            Log.e("Error", "No se puede abrir la cámara")
        }
    }
}