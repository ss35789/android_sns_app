package com.kreimben.android_sns_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(val itemList: ArrayList<ListLayout>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
        holder.content.text = itemList[position].content
        holder.created_at.text = itemList[position].created_at
        holder.title.text = itemList[position].title
        holder.user.text = itemList[position].user


    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.list_content)
        val created_at: TextView = itemView.findViewById(R.id.list_created_at)
        val title: TextView = itemView.findViewById(R.id.list_title)
        val user: TextView = itemView.findViewById(R.id.list_user)
    }
}