package com.example.pm26sactivityapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pm26sactivityapp.R
import com.example.pm26sactivityapp.entities.Group

class ListaAdapter(private val groups: List<Group>, private val listener: OnListaAdapterClickListener) : RecyclerView.Adapter<ListaAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.text_view_group_name)

        fun bind(group: Group) {
            textView.text = group.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = groups.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.bind(group)
        holder.itemView.setOnClickListener { listener.onItemClick(group) }
    }
}
