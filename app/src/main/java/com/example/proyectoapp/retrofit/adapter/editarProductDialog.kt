package com.example.proyectoapp.retrofit.adapter


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyectoapp.PantallaProductosActivity
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.instances.UserInterface.getAuthToken
import com.example.proyectoapp.retrofit.objetos.Productos
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class editarProductDialog(
    var producto: Productos,
    val onProductoEditado: (Productos) -> Unit,
    val onProductoEliminado: (Int) -> Unit
) : DialogFragment() {

    private lateinit var imageButtonLogo: ImageButton
    private var imagenCambiada=false
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.editarprodcuto, null)
        val nombre = view.findViewById<EditText>(R.id.editNombre2)
        val cantidad = view.findViewById<EditText>(R.id.editCantidad2)
        val cantidadMin = view.findViewById<EditText>(R.id.editCantidadMin2)
        val precio = view.findViewById<EditText>(R.id.editPrecio2)
        val boton = view.findViewById<Button>(R.id.btnAnadir2)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancel2)
        val descripcion = view.findViewById<EditText>(R.id.editDescripcion)
        imageButtonLogo = view.findViewById(R.id.botonFoto2)
        val btnEliminar = view.findViewById<Button>(R.id.btnCancel3)
        val habilitado = view.findViewById<CheckBox>(R.id.checkBox)
        habilitado.isChecked = producto.habilitado

        habilitado.setOnCheckedChangeListener { _, isChecked ->
            producto.habilitado = isChecked
            habilitado.setText(if (isChecked) "Habilitado" else "Deshabilitado")
        }
        nombre.inputType = InputType.TYPE_CLASS_TEXT
        descripcion.inputType = InputType.TYPE_CLASS_TEXT
        cantidad.inputType = InputType.TYPE_CLASS_NUMBER
        cantidadMin.inputType = InputType.TYPE_CLASS_NUMBER
        precio.inputType = InputType.TYPE_CLASS_NUMBER

        nombre.setText(producto.nombre)
        descripcion.setText(producto.descripcion)
        cantidad.setText(producto.stockActual.toInt().toString())
        cantidadMin.setText(producto.stockMinimo.toInt().toString())
        precio.setText(producto.precioUnitario.toInt().toString())

        cargarImagenDesdeString(producto.foto, imageButtonLogo)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        boton.setOnClickListener {
            val bitmap = (imageButtonLogo.drawable as BitmapDrawable).bitmap
            val fotoBase64 = bitmapToBase64(bitmap)

            if(imagenCambiada){
                val productoEditado = Productos(
                    producto.idProducto,
                    nombre.text.toString(),
                    precio.text.toString().toDoubleOrNull() ?: 0.0,
                    descripcion.text.toString(),
                    fotoBase64,
                    cantidad.text.toString().toIntOrNull() ?: 0,
                    cantidadMin.text.toString().toIntOrNull() ?: 0,
                    producto.habilitado
                )
                onProductoEditado(productoEditado)
            }else{
                val productoEditado = Productos(
                    producto.idProducto,
                    nombre.text.toString(),
                    precio.text.toString().toDoubleOrNull() ?: 0.0,
                    descripcion.text.toString(),
                    producto.foto,
                    cantidad.text.toString().toIntOrNull() ?: 0,
                    cantidadMin.text.toString().toIntOrNull() ?: 0,
                    producto.habilitado
                )
                onProductoEditado(productoEditado)
            }


            dialog.dismiss()
        }

        btnEliminar.setOnClickListener {
            onProductoEliminado(producto.idProducto)
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        imageButtonLogo.setOnClickListener {
            imagenCambiada=true
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
            input.startsWith("data:image", ignoreCase = true) && input.contains("base64,") -> "base64"
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
                    imageboton.setImageResource(R.drawable.cafe)
                }
            }
            else -> {
                // Imagen desconocida
                imageboton.setImageResource(R.drawable.cafe)
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