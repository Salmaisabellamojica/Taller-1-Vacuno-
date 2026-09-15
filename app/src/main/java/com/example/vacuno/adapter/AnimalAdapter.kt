package com.example.vacuno.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vacuno.R
import com.example.vacuno.model.Animal
import com.example.vacuno.model.EstadoSalud

class AnimalAdapter : ListAdapter<Animal, AnimalAdapter.AnimalViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder =
        AnimalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false))

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) = holder.bind(getItem(position))

    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val estado = itemView.findViewById<TextView>(R.id.tv_estado)

        fun bind(animal: Animal) = with(itemView) {
            findViewById<TextView>(R.id.tv_inicial).text = animal.nombre.first().uppercase()
            findViewById<TextView>(R.id.tv_id).text = "#${animal.id}"
            findViewById<TextView>(R.id.tv_nombre).text = animal.nombre
            findViewById<TextView>(R.id.tv_detalle).text =
                "${animal.raza} · ${animal.sexo.etiqueta} · ${animal.edad}"
            findViewById<TextView>(R.id.tv_peso).text = "${animal.pesoKg} kg"
            estado.text = animal.estado.etiqueta
            estado.background = chipBackground(
                if (animal.estado == EstadoSalud.SALUDABLE) "#DDF3D8" else "#FBE1E4"
            )
            estado.setTextColor(Color.parseColor(if (animal.estado == EstadoSalud.SALUDABLE) "#176B2C" else "#A7192D"))
        }

        private fun chipBackground(color: String) = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f
            setColor(Color.parseColor(color))
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Animal>() {
            override fun areItemsTheSame(oldItem: Animal, newItem: Animal) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Animal, newItem: Animal) = oldItem == newItem
        }
    }
}
