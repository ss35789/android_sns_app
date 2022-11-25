package com.kreimben.android_sns_app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class FirestoreHelper {
    private val auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val defaultProfileImg =
        "https://firebasestorage.googleapis.com/v0/b/android-sns-app.appspot.com/o/profile_images%2FN3jquxgS7rSLhI0M1KfgBieZVP73_Sat%20Nov%2012%2010%3A22%3A57%20GMT%202022?alt=media&token=248542a1-7855-417b-89f2-7ee739688c72"

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

                        if (currentUser.displayName != "") {
                            doc.update("displayname", currentUser.displayName)
                                .addOnFailureListener {
                                    Log.e(null, it.message.toString())
                                }
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
        var currentUserImg = currentUser?.photoUrl
        if (currentUserImg == null) {
            currentUserImg = Uri.parse(defaultProfileImg)
        }
        db.collection("user").document(currentUser!!.uid).set(
            hashMapOf(
                "email" to currentUser.email,
                "uid" to currentUser.uid,
                "displayname" to "Unknown ${Date().time}",
                "following" to mutableListOf<String>(),
                "photourl" to currentUserImg
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
                if ((f as MutableList<String>).contains(followingUID)) {
                    f.remove(followingUID)
                    btn.text = "Follow"
                    btn.setBackgroundColor(Color.parseColor("#2EFE9A"))
                    btn.setTextColor(Color.parseColor("#000000"))
                } else {
                    f.add(followingUID)
                    btn.text = "UnFollow"
                    btn.setBackgroundColor(Color.parseColor("#050fff"))
                    btn.setTextColor(Color.parseColor("#FFFFFF"))
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
                if ((f as MutableList<String>).contains(followingUID)) {
                    btn.text = "UnFollow"
                    btn.setBackgroundColor(Color.parseColor("#050fff"))
                    btn.setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    btn.text = "Follow"
                    btn.setBackgroundColor(Color.parseColor("#2EFE9A"))
                    btn.setTextColor(Color.parseColor("#000000"))
                }

                Log.d(null, f.toString())

            }
        }
    }




    fun Signout(){
        FirebaseAuth.getInstance().signOut();
    }

    fun getDisplayName(uid: String, textView: TextView) {
        val doc = db.collection("user").document(uid)

        doc.get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result.exists()) {
                    textView.text = it.result.data!!.get("displayname").toString()
                }
            }
            .addOnFailureListener {
                Log.e(null, "Fail to fetch displayname: ${it.message}")
            }
    }

    fun getEmail(uid: String, textView: TextView) {
        val doc = db.collection("user").document(currentUser!!.uid)

        doc.get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result.exists()) {
                    textView.text = it.result.data!!.get("email").toString()
                }
            }
            .addOnFailureListener {
                Log.e(null, "Fail to fetch email: ${it.message}")
            }
    }

    fun removePost(documentId: String, complete: (is_success: Boolean) -> Unit) {
        val doc = db.collection("post").document(documentId)

        doc.get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result.exists()) {
                    // 먼저 내가 쓴 글인지 확인 함.
                    if (it.result.data!!.get("user") != currentUser!!.uid) {
                        // 내가 쓴 글이 아니면 콜백에 false를 넣어 줌.
                        complete(false)
                    } else {
                        // 내가 쓴 글이 맞다면 지우고 콜백에 최종 상태 보내 줌.
                        doc.delete()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    complete(true)
                                }
                            }
                            .addOnFailureListener {
                                Log.e(null, "Fail to delete document: ${documentId}")
                                complete(false)
                            }
                    }
                }
            }
    }

    fun deleteAccount(
        email: String,
        password: String,
        complete: (is_success: Boolean, message: String?) -> Unit
    ) {
        // Remove All of Posts I posted
        db.collection("post").get()
            .addOnCompleteListener {
                if (it.isSuccessful && !it.result.isEmpty) {
                    val listOfPosts = mutableListOf<String>()

                    for (post in it.result.documents) {
                        if (post.data!!.get("user") == currentUser!!.uid) {
                            listOfPosts.add(post.id)
                        }
                    }

                    if (listOfPosts.isNotEmpty()) {
                        for (doc_id in listOfPosts) {
                            db.collection("post").document(doc_id).delete()
                                .addOnFailureListener {
                                    println("Fail to delete post: ${it.message}")
                                    complete(false, null)
                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                println("Fail to get POSTs: ${it.message}")
                complete(false, null)
            }

        // Delete user data on "user" document.
        db.collection("user").document(currentUser!!.uid).delete()
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    println("Fail to delete user profile document isSuccessful")
                    complete(false, null)
                }
            }
            .addOnFailureListener {
                println("Fail to delete user profile document: ${it.message}")
                complete(false, null)
            }


        // Delete Account
        currentUser.reauthenticate(
            EmailAuthProvider.getCredential(email, password)
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser.delete()
                        .addOnCompleteListener {
                            println("delete user result: ${it.isSuccessful} ${it.isComplete}")
                            complete(it.isSuccessful, null)
                        }
                        .addOnFailureListener {
                            println("Fail to delete user")
                            complete(false, it.message)
                        }
                } else {
                    complete(false, "이메일과 비밀번호를 다시 한번 확인 해주세요.")
                }
            }
    }
}

