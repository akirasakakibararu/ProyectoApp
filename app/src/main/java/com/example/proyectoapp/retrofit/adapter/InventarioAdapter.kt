package com.example.proyectoapp.retrofit.adapter

import android.view.LayoutInflater
import com.example.proyectoapp.R
import com.example.proyectoapp.retrofit.pojos.Productos

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventarioAdapter(
    private var productos: List<Productos>
) : RecyclerView.Adapter<InventarioAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val stockActual: TextView = view.findViewById(R.id.tvStockActual)
        val stockMinimo: TextView = view.findViewById(R.id.tvStockMinimo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.stockActual.text = producto.stockActual.toString()
        holder.stockMinimo.text = producto.stockMinimo.toString()
    }

    override fun getItemCount(): Int = productos.size

    fun updateLista(nuevaLista: List<Productos>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}
