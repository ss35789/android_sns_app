package com.kreimben.android_sns_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kreimben.android_sns_app.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        auth = Firebase.auth

        binding.signinButton.setOnClickListener {
           if(CheckPassword(binding.passwordSignupEdittext.text.toString()
               ,binding.passwordCheckSignupEdittext.text.toString()))
           {
            this.createUser(
                binding.emailSignupEdittext.text.toString(),
                binding.passwordSignupEdittext.text.toString()
            )
            goSigninActivity()
           }
        }

    }
    fun CheckPassword(password: String, passwordCheck: String): Boolean{
        if(!password.equals(passwordCheck)){
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
    fun createUser(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "회원가입 실패, 비밀번호가 너무 단순합니다", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goSigninActivity() {
        startActivity(Intent(this, SigninActivity::class.java))
        finish()
    }

}