package com.kreimben.android_sns_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kreimben.android_sns_app.databinding.ActivityDeleteAccountBinding
import java.lang.Thread.sleep

class DeleteAccount : AppCompatActivity() {
    lateinit var binding: ActivityDeleteAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.deleteButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.pwEditText.text.toString()
            println("email: ${email} password: ${password}")

            FirestoreHelper().deleteAccount(
                email,
                password
            ) { is_success: Boolean, message: String? ->
                if (is_success) {
                    Toast.makeText(
                        this,
                        message ?: "탈퇴 완료",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    sleep(1000)
                    finishAffinity()
                } else {
                    Toast.makeText(
                        this,
                        message ?: "탈퇴 실패",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }
}