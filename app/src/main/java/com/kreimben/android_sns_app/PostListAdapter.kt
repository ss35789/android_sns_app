package com.kreimben.android_sns_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class PostListAdapter(val itemList: ArrayList<PostListLayout>): RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uid = itemList[position].uid
        FirestoreHelper().getDisplayName(uid.toString(), holder.user)

        val imageUrl: String? = itemList[position].image_url
        holder.content.text = " content : " + itemList[position].content
        holder.created_at.text = " created_at : " + itemList[position].created_at.toString()
        holder.title.text = " title : " + itemList[position].title
        Glide.with(holder.imageView.context).load(imageUrl).into(holder.imageView)

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.list_name)
        val created_at: TextView = itemView.findViewById(R.id.list_created_at)
        val title: TextView = itemView.findViewById(R.id.list_title)
        val user: TextView = itemView.findViewById(R.id.list_user)
        val imageView: ImageView = itemView.findViewById(R.id.list_imageView)
    }
}