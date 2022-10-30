package com.kreimben.android_sns_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kreimben.android_sns_app.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {
    /*
    `SingInActivity`로 했을 때 duplicated 에러가 떠서
    어쩔 수 없이 `SigninActivity`로 명명함.
     */
    private lateinit var binding: ActivitySigninBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signinButton.setOnClickListener {
            this.signIn(
                binding.emailLoginEdittext.text.toString(),
                binding.passwordLoginEdittext.text.toString()
            )
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "로그인을 할 수 없습니다.\n다시 한번 확인 하세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}