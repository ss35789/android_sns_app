package com.kreimben.android_sns_app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreHelper {
    private var db: FirebaseFirestore = Firebase.firestore

    fun updateProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            db.collection("user").document(currentUser.email.toString()).set(
                hashMapOf(
                    "email" to currentUser.email,
                    "uid" to currentUser.uid,
                    "displayname" to currentUser.displayName,
                    "photourl" to currentUser.photoUrl
                )
            ).addOnCompleteListener {
                println("currentUser upload completed!")
            }.addOnFailureListener {
                println("currentUser upload failed!")
            }
        }
    }
}