package com.kreimben.android_sns_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kreimben.android_sns_app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    val itemList = arrayListOf<PostListLayout>()
    val adapter = PostListAdapter(itemList)
    lateinit var mysrl: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 테스트용 firestore 인스턴스를 생성했으니 필요없으면 지울것.
        this.db = Firebase.firestore

        // 프로필 업데이트함
        FirestoreHelper().updateProfile()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = adapter
        binding.Myprofile.text = FirebaseAuth.getInstance().currentUser?.email
        binding.postButton.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
        binding.friendsButton.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        mysrl = binding.contentSrl
        mysrl.setOnRefreshListener(OnRefreshListener { // 새로고침시 동작
            getPost()

            // 종료
            mysrl.setRefreshing(false)
        })

        getPost()
    }


    override fun onRestart() {
        super.onRestart()
        getPost()
    }

    private fun getPost() {
        db.collection("post").orderBy("created_at", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                // 성공할 경우
                itemList.clear()
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    val item = ListLayout(
                        document["content"] as String?,
                        document["created_at"] as com.google.firebase.Timestamp?,
                        document["image_uri"] as String?,
                        document["title"] as String?,
                        document["user"] as String?
                    )

                    itemList.add(item)
                }
            }.addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }.addOnCompleteListener {
                adapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
            }
    }
}