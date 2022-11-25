package com.kreimben.android_sns_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        auth = Firebase.auth
        binding.btnGoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        if (FirebaseAuth.getInstance().currentUser!= null) {
            // 자동 로그인 기능.
            goMainActivity()
        } else {
            binding.signinButton.setOnClickListener {
                this.signIn(
                    binding.emailSigninEdittext.text.toString(),
                    binding.passwordSigninEdittext.text.toString()
                )
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    goMainActivity()
                } else {
                    Toast.makeText(
                        this,
                        "로그인을 할 수 없습니다.\n다시 한번 확인 하세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun goMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}