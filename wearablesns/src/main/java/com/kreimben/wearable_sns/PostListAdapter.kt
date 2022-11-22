package com.kreimben.wearable_sns

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class PostListAdapter(val itemList: ArrayList<PostListLayout>) :
    RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_list_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uid = itemList[position].uid
        FirestoreHelper().getDisplayName(uid.toString(), holder.user)

        val imageUrl: String? = itemList[position].image_url
        holder.content.text = itemList[position].content
        holder.created_at.text = getDateTime(itemList[position].created_at!!)
        holder.title.text = itemList[position].title
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.imageView.context).load(imageUrl).into(holder.imageView)
        } else {
            holder.imageView.layoutParams.height = 0
            holder.imageView.layoutParams.width = 0

            holder.imageView.requestLayout()
        }
    }

    private fun getDateTime(s: Timestamp): String {
        val milliseconds = s.seconds * 1000 + s.nanoseconds / 1000000
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate).toString()
        Log.d("TAG170", date)

        return date
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.list_name)
        val created_at: TextView = itemView.findViewById(R.id.list_created_at)
        val title: TextView = itemView.findViewById(R.id.list_title)
        val user: TextView = itemView.findViewById(R.id.list_user)
        val imageView: ImageView = itemView.findViewById(R.id.list_imageView)
    }
}