package com.kreimben.android_sns_app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kreimben.android_sns_app.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 테스트용 firestore 인스턴스를 생성했으니 필요없으면 지울것.
        this.db = Firebase.firestore

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firestoreTestButton.setOnClickListener {
            this.firestoreTest()
        }

        binding.firestoreShowAllDataButton.setOnClickListener {
            this.showAllTestData(binding.displayTest)
        }
    }

    private fun firestoreTest() {
        val data = hashMapOf(
            "provider" to "kreimben",
            "implementation" to "kreimben",
            "date" to Date()
        )

        db.collection("test_db")
            .add(data)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener {
                println("Error adding document: ${it.message}")
            }
    }

    private fun showAllTestData(textView: TextView) {
        db.collection("test_db")
            .get()
            .addOnSuccessListener {
                println("success to get all data: ${it}")

                val result = StringBuilder()

                for (document in it) {
                    result.append("id: ${document.id}\n")
                        .append("data: ${document.data}\n\n\n")
                }

                textView.text = result.toString()
            }
            .addOnFailureListener {
                println("Error get document: ${it.message}")
            }
    }
}