package com.example.vacuno.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vacuno.R
import com.example.vacuno.model.Inventario

class InventarioAdapter : ListAdapter<Inventario, InventarioAdapter.InventarioViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioViewHolder =
        InventarioViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_inventario, parent, false))

    override fun onBindViewHolder(holder: InventarioViewHolder, position: Int) = holder.bind(getItem(position))

    class InventarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Inventario) = with(itemView) {
            findViewById<ImageView>(R.id.tv_inventario_inicial).setImageResource(R.drawable.logo_vacuno)
            findViewById<TextView>(R.id.tv_inventario_nombre).text = item.nombre
            findViewById<TextView>(R.id.tv_inventario_categoria).text = item.categoria.etiqueta
            findViewById<TextView>(R.id.tv_inventario_cantidad).text = "${item.cantidad} ${item.unidad}"
            val stock = findViewById<TextView>(R.id.tv_inventario_stock)
            stock.text = if (item.requiereReposicion) "Reponer" else "Stock suficiente"
            stock.setTextColor(Color.parseColor(if (item.requiereReposicion) "#A7192D" else "#176B2C"))
            stock.background = chipBackground(if (item.requiereReposicion) "#FBE1E4" else "#DDF3D8")
        }

        private fun chipBackground(color: String) = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
            setColor(Color.parseColor(color))
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Inventario>() {
            override fun areItemsTheSame(oldItem: Inventario, newItem: Inventario) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Inventario, newItem: Inventario) = oldItem == newItem
        }
    }
}
