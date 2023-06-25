package com.app.sbook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.sbook.AddBookActivity
import com.app.sbook.MainActivity
import com.app.sbook.R
import com.app.sbook.holder.ViewHolder
import com.app.sbook.model.Libro

class LibroAdapter(
    private val listActivity: MainActivity,
    private val modelList: List<Libro>
    // private val context: Context
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_layout, parent, false)

        val viewHolder = ViewHolder(itemView)
        viewHolder.setOnClickListener(object : ViewHolder.ClickListener {
            override fun onItemClick(view: View, position: Int) {
                val nombre: String? = modelList[position].nombre
                val autor: String? = modelList[position].autor
                val anio: Int? = modelList[position].anio
                // Toast.makeText(this, nombre, Toast.LENGTH_LONG).show()
            }

            override fun onItemLongClick(view: View, position: Int) {
                val options = arrayOf("Actualizar", "Eliminar")
                val builder = AlertDialog.Builder(listActivity)

                val nombreLibro: String? = modelList[position].nombre
                builder.setTitle(nombreLibro)

                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Accion para la opcion "Actualizar"
                            val id: String? = modelList[position].id
                            val nombre: String? = modelList[position].nombre
                            val autor: String? = modelList[position].autor
                            val anio: Int? = modelList[position].anio

                            val intent = Intent(listActivity, AddBookActivity::class.java)
                            intent.putExtra("pId", id)
                            intent.putExtra("pNombre", nombre)
                            intent.putExtra("pAutor", autor)
                            intent.putExtra("pAnio", anio)
                            listActivity.startActivity(intent)
                        }
                        1 -> {
                            // Accion para la opcion "Eliminar"
                            showConfirmationDelete(position)
                        }
                    }
                }.create().show()
            }
        })
        return viewHolder
    }

    private fun showConfirmationDelete(position: Int) {
        val nombre: String? = modelList[position].nombre
        val builder = AlertDialog.Builder(listActivity)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que deseas eliminar el siguiente libro?\n\n $nombre")

        builder.setPositiveButton("Eliminar") { dialog, which ->
            listActivity.deleteData(position)
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.mNombre?.text = modelList[i].nombre
        holder.mAutor?.text = modelList[i].autor
        holder.mAnio?.text = modelList[i].anio.toString()
    }

    override fun getItemCount(): Int {
        return modelList.size
    }
}