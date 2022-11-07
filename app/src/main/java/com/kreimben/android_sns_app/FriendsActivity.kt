package com.kreimben.android_sns_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kreimben.android_sns_app.databinding.ActivityFriendsBinding


class FriendsActivity: AppCompatActivity() {
    lateinit var mysrl: SwipeRefreshLayout;
    private lateinit var binding: ActivityFriendsBinding
    private lateinit var db: FirebaseFirestore
    val itemList = arrayListOf<FriendsListLayout>()
    val adapter = FriendListAdapter(itemList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = Firebase.firestore

        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.friendsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.friendsList.adapter = adapter

        mysrl = binding.contentSrl
        mysrl.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { // 새로고침시 동작
            GetUserList()

            // 종료
            mysrl.setRefreshing(false)
        })



        GetUserList()
    }
    override fun onRestart() {

        super.onRestart()
        GetUserList()
    }

    private fun GetUserList(){

        db.collection("User")
            .orderBy("name", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                // 성공할 경우
                itemList.clear()
                for (document in result) {  // 가져온 문서들은 result에 들어감


                    val item = FriendsListLayout(
                        document["email"] as String?
                        , document["name"] as String?
                        , document["uid"] as String?

                    )
                    if(FirebaseAuth.getInstance().currentUser?.uid == item.uid)continue
                    itemList.add(item)
                }

                adapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("FriendsActivity", "Error getting documents: $exception")
            }


    }
}