package com.app.sbook.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.sbook.R

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mNombre: TextView? = null
    var mAutor: TextView? = null
    var mAnio: TextView? = null
    var mView: View? = null

    init {
        mView = itemView

        itemView.setOnClickListener {
            mClickListener?.onItemClick(it, adapterPosition)
        }

        itemView.setOnLongClickListener {
            mClickListener?.onItemLongClick(it, adapterPosition)
            true
        }

        // initialize views with model_layout.xml
        mNombre = itemView.findViewById(R.id.lblLibro)
        mAutor = itemView.findViewById(R.id.lblAutor)
        mAnio = itemView.findViewById(R.id.lblAnio)
    }

    private var mClickListener: ClickListener? = null

    interface ClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    fun setOnClickListener(clickListener: ClickListener) {
        mClickListener = clickListener
    }
}