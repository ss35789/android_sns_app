package com.kreimben.android_sns_app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class FriendListAdapter(val itemList: ArrayList<UserListLayout>): RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_list_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var buttonStatus = false
        holder.displayname.text = " name : " + itemList[position].displayname
        holder.email.text = " email : " + itemList[position].email
        holder.follow_button.setOnClickListener {
            //친구추가
            //FirestoreHelper().updateFollwing(itemList[position].email)
            if(buttonStatus){
                //친구추가
                FirestoreHelper().addFollower(itemList[position].uid)
                buttonStatus= false
                holder.follow_button.setBackgroundColor(Color.parseColor("#ff111f"))
                holder.follow_button.setText("UnFollow")
            }
            else{
                FirestoreHelper().removeFollower(itemList[position].uid)
                buttonStatus= true
                holder.follow_button.setBackgroundColor(Color.parseColor("#050fff"))
                holder.follow_button.setText("Follow")
            }

        }

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val displayname: TextView = itemView.findViewById(R.id.list_name)
        val email: TextView = itemView.findViewById(R.id.list_email)
        val follow_button: Button = itemView.findViewById(R.id.follow_button)
    }
}