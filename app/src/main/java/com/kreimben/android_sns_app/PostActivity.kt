package com.kreimben.android_sns_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kreimben.android_sns_app.databinding.ActivityPostBinding
import java.util.*

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private val db: FirebaseFirestore = Firebase.firestore
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var imageURL: String? = null
    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 갤러리에서 이미지를 가져온 바로 직후, 처리를 해주는 코드
        if (it.resultCode == Activity.RESULT_OK) {
            binding.imageView.setImageURI(it.data?.data)
            uploadImage(it.data?.data!!) // 바로 이미지를 업로드하게 함.
        } else {
            // 처리 결과가 OK이지 않을 떄
            Toast.makeText(
                this,
                "Result not ok.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

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

        binding.pickImageButton.setOnClickListener {
            // 다른 이미지를 업로드 하고 싶을 때 메모리 상에 있는 데이터를 지움.
            this.imageURL = null
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        imageResult.launch(intent) // 이미지를 가져오는 인텐트 실행. (이후 이미지 업로드는 이 함수 밖에서 일어남)
    }

    private fun uploadPost(title: String, content: String) {
        if (binding.imageView.drawable != null && this.imageURL == null) {
            // 상당히 별로인 방식이지만, 어쩔 수 없이 이미지 업로드가 다 완료가 됐는지 확인 하는 코드.
            // `binding.imageView.drawable`에서 이미지 뷰에 이미지가 삽입 됐는지 확인 하는 코드.
            // `binding.imageView.drawable`이 null이라면 처음부터 포스팅 할 때 이미지를 업로드 하고 싶지 않은 것.
            Toast.makeText(
                this,
                "이미지가 업로드 될 때까지 기다려 주십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val data = hashMapOf(
            "title" to title,
            "content" to content,
            "user" to FirebaseAuth.getInstance().uid, // 현재 로그인 된 유저 정보를 업로드합니다.
            "created_at" to Date(),
            "image_uri" to this.imageURL
        ) // HashMap(딕셔너리)으로 데이터를 설정함.

        db.collection("post")
            .add(data)
            .addOnCompleteListener {
                Toast.makeText(
                    this,
                    "업로드 완료!",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish() // 업로드가 성공한다면 이 화면을 종료하고 메인 페이지로 돌아감.
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

    private fun uploadImage(uri: Uri) {
        // 순수하게 이미지만 업로드하는 함수.
        val fileName = StringBuilder()
            .append(FirebaseAuth.getInstance().uid)
            .append("_")
            .append(Date())
            .toString()

        val imagesRef = storage.reference.child("images/").child(fileName)

        imagesRef.putFile(uri)
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "이미지 업로드 실패...\n${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnCompleteListener {
                it.result.storage.downloadUrl.addOnCompleteListener {
                    this.imageURL = it.result.toString()
                }

                Toast.makeText(this, "이미지 업로드 완료!", Toast.LENGTH_SHORT).show()
            }
    }
}