package com.kreimben.android_sns_app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class PostListAdapter(
    val itemList: ArrayList<PostListLayout>,
    val context: Context
) : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

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

        Glide.with(holder.imageView.context).load(imageUrl).into(holder.imageView)
        if(imageUrl == null ){
            val params: ViewGroup.LayoutParams = holder.imageView.getLayoutParams() as ViewGroup.LayoutParams
            params.width = 0
            params.height = 0

            holder.imageView.setLayoutParams(params)
        }
        //holder.imageView.height
        // 드래그 새로고침 하면 어떤건 이미지 나오고 어떤건 안나오고 반복 버그 생김

        // 추가 메뉴 설정
        holder.more_button.setOnClickListener {
            val popup = PopupMenu(context, holder.more_button)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.post_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                val doc_id = itemList[position].documentId.toString()
                when (it.itemId) {
                    R.id.remove_this_post -> {
                        FirestoreHelper().removePost(doc_id) {
                            println("success to remove post.")

                            if (it == true) {
                                this.refresh()
                            } else {
                                this.makeText("자신이 쓴 글만 삭제 할 수 있습니다.")
                            }
                        }
                        true
                    }
                    else -> {
                        println("No Way!")
                        false
                    }
                }
            }
            popup.show()
        }
    }

    lateinit var refresh: () -> Unit
    lateinit var makeText: (message: String) -> Unit

    private fun getDateTime(s: Timestamp): String {
        val milliseconds = s.seconds * 1000 + s.nanoseconds / 1000000
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
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
        val more_button: ImageButton = itemView.findViewById(R.id.more_button)
    }
}