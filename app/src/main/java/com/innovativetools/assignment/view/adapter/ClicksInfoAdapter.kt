package com.innovativetools.assignment.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innovativetools.assignment.R
import com.innovativetools.assignment.data.model.ClickInfoItem

class ClicksInfoAdapter(private var items: List<ClickInfoItem>) :
    RecyclerView.Adapter<ClicksInfoAdapter.ClicksInfoViewHolder>() {

    class ClicksInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val count: TextView = itemView.findViewById(R.id.count)
        val label: TextView = itemView.findViewById(R.id.label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClicksInfoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_click_info, parent, false)
        return ClicksInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClicksInfoViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconRes)
        holder.count.text = item.count
        holder.label.text = item.label
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItem: List<ClickInfoItem>) {
        items = newItem
        notifyDataSetChanged()
    }
}
