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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.objetos.Productos
import java.io.ByteArrayOutputStream

class anadirProductDialog(
    val onProductoAñadido: (Productos) -> Unit
) : DialogFragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoURI: Uri
    private lateinit var imageButtonLogo: ImageButton
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.anadirproducto, null)

        val nombre = view.findViewById<EditText>(R.id.editNombre)
        val cantidad = view.findViewById<EditText>(R.id.editCantidad)
        val cantidadMin = view.findViewById<EditText>(R.id.editCantidadMin)
        val precio = view.findViewById<EditText>(R.id.editPrecio)
        val boton = view.findViewById<Button>(R.id.btnAnadir)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancel)
        imageButtonLogo = view.findViewById(R.id.botonFoto)
        nombre.inputType = InputType.TYPE_CLASS_TEXT

        cantidad.inputType = InputType.TYPE_CLASS_NUMBER
        cantidadMin.inputType = InputType.TYPE_CLASS_NUMBER
        precio.inputType = InputType.TYPE_CLASS_NUMBER
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        boton.setOnClickListener {
            val bitmap = (imageButtonLogo.drawable as BitmapDrawable).bitmap//Obtenemos el bitmap de la imagen
            val fotoBase64 = bitmapToBase64(bitmap)//Convertimos el bitmap a Base64
            val producto = Productos(
                0,
                nombre.text.toString(),
                precio.text.toString().toDoubleOrNull() ?: 0.0,
                "", fotoBase64,
                cantidad.text.toString().toIntOrNull() ?: 0,
                cantidadMin.text.toString().toIntOrNull() ?: 0,
                true



            )
            onProductoAñadido(producto)
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        imageButtonLogo.setOnClickListener {
            takePicture()
        }
        // Lista de todos los EditTexts a limpiar
        val editTexts = listOf(
            nombre,
            cantidad,
            cantidadMin,
            precio
        )
        // Aplicar la función de limpieza a todos los EditTexts
        setOnFocusClearListener(editTexts)
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
    fun base64ToBitmap(base64Str: String): Bitmap {//Función para convertir un Base64 a bitmap
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
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
    fun setOnFocusClearListener(editTexts: List<EditText>) {
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    editText.text.clear()
                }
            }
        }
    }
}