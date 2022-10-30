package com.kreimben.android_sns_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kreimben.android_sns_app.databinding.ActivityPostBinding
import java.util.*

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postUploadButton.setOnClickListener {
            this.uploadPost(
                binding.postTitle.text.toString(),
                binding.postContent.text.toString()
            )
        }
    }

    private fun uploadPost(title: String, content: String) {
        val data = hashMapOf(
            "title" to title,
            "content" to content,
            "user" to FirebaseAuth.getInstance().uid, // 현재 로그인 된 유저 정보를 업로드합니다.
            "created_at" to Date()
        )

        db.collection("post")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "업로드 성공!",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "포스트 업로드에 실패 하였습니다.\n${it.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
    }
}