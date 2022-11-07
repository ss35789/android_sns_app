package com.kreimben.android_sns_app

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.kreimben.android_sns_app.databinding.ActivityFriendsBinding
import com.kreimben.android_sns_app.databinding.ActivityPostBinding

class FriendsActivity: AppCompatActivity() {
    private lateinit var binding: ActivityFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}