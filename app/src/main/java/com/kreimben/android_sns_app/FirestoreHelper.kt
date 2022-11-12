package com.kreimben.android_sns_app

import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreHelper {
    private var db: FirebaseFirestore = Firebase.firestore

    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun updateProfile() {
        if (currentUser != null) {
            val doc = db.collection("user").document(currentUser.uid)

            doc.get()
                .addOnCompleteListener {

                    if (it.isSuccessful && it.result.exists()) {
                        doc.update("email", currentUser.email)
                            .addOnFailureListener {
                                Log.e(null, it.message.toString())
                            }

                        doc.update("displayname", currentUser.displayName)
                            .addOnFailureListener {
                                Log.e(null, it.message.toString())
                            }

                        doc.update("photourl", currentUser.photoUrl)
                            .addOnFailureListener {
                                Log.e(null, it.message.toString())
                            }

                    } else {
                        this.createProfile()
                    }
                }
        }
    }

    private fun createProfile() {
        db.collection("user").document(currentUser!!.uid).set(
            hashMapOf(
                "email" to currentUser.email,
                "uid" to currentUser.uid,
                "displayname" to currentUser.displayName,
                "following" to null,
                "photourl" to currentUser.photoUrl
            )
        ).addOnCompleteListener {
            println("currentUser upload completed!")
        }.addOnFailureListener {
            println("currentUser upload failed!")
        }
    }

    fun updateFollower(followingUID: String, btn: Button) {
        val doc = db.collection("user").document(currentUser!!.uid)

        doc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                var f = it.result.data?.get("following")
                if (f == null) {
                    f = mutableListOf<String>()
                }

                Log.d(null, f.toString())
                if((f as MutableList<String>).contains(followingUID)){
                    (f as MutableList<String>).remove(followingUID)
                    btn.setText("Follow")
                }
                else{
                    (f as MutableList<String>).add(followingUID)
                    btn.setText("UnFollow")
                }

                Log.d(null, f.toString())

                doc.update(
                    "following", f
                )
                    .addOnFailureListener {
                        Log.e(null, it.message.toString())
                    }
            }
        }
    }





    fun checkFollowing(followingUID: String, btn: Button) {
        val doc = db.collection("user").document(currentUser!!.uid)

        doc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                var f = it.result.data?.get("following")
                if (f == null) {
                    f = mutableListOf<String>()
                }
                Log.d(null, f.toString())
                if((f as MutableList<String>).contains(followingUID)){
                    btn.setText("UnFollow")
                }
                else{
                    btn.setText("Follow")
                }

                Log.d(null, f.toString())

            }
        }
    }

}

