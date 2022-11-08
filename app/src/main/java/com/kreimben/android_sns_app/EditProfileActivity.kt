package com.kreimben.android_sns_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kreimben.android_sns_app.databinding.ActivityEditProfileBinding
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var imageURL: String? = null

    //    private val db: FirebaseFirestore = Firebase.firestore
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
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
                this, "Result not ok.", Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfile()

        binding.finishButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val displayName = binding.nicknameEditText.text.toString()

                val request = UserProfileChangeRequest.Builder().setDisplayName(displayName)

                if (binding.imageView.drawable != null && this.imageURL != null) {
                    request.photoUri = Uri.parse(this.imageURL)
                }

                currentUser.updateProfile(
                    request.build()
                ).addOnSuccessListener {
                    Toast.makeText(this, "프로필 수정 성공!", Toast.LENGTH_SHORT).show()
                }.addOnCompleteListener {
                    FirestoreHelper().updateProfile()

                    finish()
                }.addOnFailureListener {
                    Toast.makeText(
                        this, "네트워크 통신에 에러가 발생했습니다.\n${it.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.imageView.setOnClickListener {
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

    private fun uploadImage(uri: Uri) {
        // 순수하게 이미지만 업로드하는 함수.
        val fileName =
            StringBuilder().append(FirebaseAuth.getInstance().uid).append("_").append(Date())
                .toString()

        val imagesRef = storage.reference.child("profile_images/").child(fileName)

        imagesRef.putFile(uri).addOnFailureListener {
            Toast.makeText(
                this, "이미지 업로드 실패...\n${it.message}", Toast.LENGTH_LONG
            ).show()
        }.addOnCompleteListener {
            it.result.storage.downloadUrl.addOnCompleteListener {
                this.imageURL = it.result.toString()

                Toast.makeText(this, "이미지 업로드 완료!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadImage(c: Activity, imageURL: String, imageView: ImageView) {
        // 기존에 설정된 이미지를 불러옴.
        Glide.with(c)
            .load(imageURL) // 불러올 이미지 url
            .into(imageView) // 이미지를 넣을 뷰
    }

    private fun loadProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
                Firebase.firestore.collection("user").document(email).get()
                    .addOnSuccessListener {
                        if (it != null) {
                            val nickname = it.data!!.get("displayname").toString()
                            binding.nicknameEditText.setText(nickname)
                            loadImage(this, it.data!!.get("photourl").toString(), binding.imageView)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "프로필 가져오기 실패\n${it.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }
        }
    }
}