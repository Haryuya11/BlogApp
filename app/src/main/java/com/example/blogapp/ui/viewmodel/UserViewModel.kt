package com.example.blogapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.blogapp.data.Comment
import com.example.blogapp.data.UserData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class UserViewModel : ViewModel() {
    private val databaseReference =
        FirebaseDatabase.getInstance("https://blogapp-43766-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()


    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onResult(true, null)
                    } else {
                        auth.signOut()
                        onResult(false, "Please verify your email address")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        imageUri: Uri?,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            user.let {
                                val userReference = databaseReference.child("users")
                                val userId = user.uid
                                val userData = UserData(name, email)
                                userReference.child(userId).setValue(userData)
                                val storageReference =
                                    storage.reference.child("profile_images/$userId.jpg")
                                storageReference.putFile(imageUri!!)
                                    .addOnCompleteListener { uploadTask ->
                                        if (uploadTask.isSuccessful) {
                                            storageReference.downloadUrl.addOnCompleteListener { uri ->
                                                if (uri.isSuccessful) {
                                                    val profileImage = uri.result.toString()
                                                    userReference.child(userId)
                                                        .child("profileImage")
                                                        .setValue(profileImage)
                                                    onResult(true, null)
                                                } else {
                                                    onResult(false, uri.exception?.message)
                                                }
                                            }
                                        } else {
                                            onResult(false, uploadTask.exception?.message)
                                        }
                                    }
                            }
                        } else {
                            onResult(false, verificationTask.exception?.message)
                        }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loadUserProfile(userId: String, onResult: (String, String) -> Unit) {
        databaseReference.child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                        ?: ""
                    onResult(userName, profileImage)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun loadUserInfo(
        userId: String,
        onResult: (String, String, String, String?, String?, String?, String?) -> Unit
    ) {
        databaseReference.child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val profileImage =
                        snapshot.child("profileImage").getValue(String::class.java) ?: ""
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val dob = snapshot.child("dob").getValue(String::class.java)
                    val gender = snapshot.child("gender").getValue(String::class.java)
                    val bio = snapshot.child("hobbies").getValue(String::class.java)
                    val country = snapshot.child("country").getValue(String::class.java)
                    onResult(userName, profileImage, email, dob, gender, bio, country)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, updateTask.exception?.message)
                        }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
        } else {
            onResult(false, "User not found")
        }
    }

    fun updateUserData(
        userId: String,
        userName: String,
        profileImageUri: Uri?,
        dob: String?,
        gender: String?,
        hobbies: String?,
        country: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (profileImageUri != null) {
            val profileImageRef = storage.reference.child("profile_images/$userId.jpg")
            profileImageRef.putFile(profileImageUri)
                .addOnSuccessListener {
                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        val profileImageUrl = uri.toString()
                        val userUpdates = mapOf(
                            "name" to userName,
                            "profileImage" to profileImageUrl,
                            "dob" to dob,
                            "gender" to gender,
                            "hobbies" to hobbies,
                            "country" to country
                        )

                        databaseReference.child("users").child(userId)
                            .updateChildren(userUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, task.exception?.message)
                                }
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    onResult(false, exception.message)
                }
        } else {
            val userUpdates = mapOf(
                "name" to userName,
                "dob" to dob,
                "gender" to gender,
                "hobbies" to hobbies,
                "country" to country
            )

            databaseReference.child("users").child(userId)
                .updateChildren(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }

    fun updateUserDetailsInBlogs(
        userId: String,
        newUserName: String,
        newProfileImageUri: Uri?,
        onComplete: (Boolean, String) -> Unit
    ) {
        if (newProfileImageUri != null) {
            val profileImageRef = storage.reference.child("profile_images/$userId.jpg")
            profileImageRef.putFile(newProfileImageUri)
                .addOnSuccessListener {
                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        val newProfileImageUrl = uri.toString()
                        updateBlogs(
                            userId,
                            newUserName,
                            newProfileImageUrl,
                            onComplete
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    onComplete(false, exception.message ?: "Error uploading profile image")
                }
        } else {
            updateBlogs(userId, newUserName, null, onComplete)
        }
    }

    private fun updateBlogs(
        userId: String,
        newUserName: String,
        newProfileImageUrl: String?,
        onComplete: (Boolean, String) -> Unit
    ) {
        databaseReference.child("blogs")
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                for (blogSnapshot in snapshot.children) {
                    blogSnapshot.ref.child("userName").setValue(newUserName)
                    if (newProfileImageUrl != null) {
                        blogSnapshot.ref.child("profileImage").setValue(newProfileImageUrl)
                    }
                }
                onComplete(true, "User details updated in all blogs")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error updating user details in blogs")
            }
    }

    fun updateUserDetailsInComments(
        userId: String,
        newUserName: String,
        newProfileImageUri: Uri?,
        onComplete: (Boolean, String) -> Unit
    ) {
        if (newProfileImageUri != null) {
            val profileImageRef = storage.reference.child("profile_images/$userId.jpg")
            profileImageRef.putFile(newProfileImageUri)
                .addOnSuccessListener {
                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        val newProfileImageUrl = uri.toString()
                        updateComments(userId, newUserName, newProfileImageUrl, onComplete)
                    }
                }
                .addOnFailureListener { exception ->
                    onComplete(false, exception.message ?: "Error uploading profile image")
                }
        } else {
            updateComments(userId, newUserName, null, onComplete)
        }
    }

    private fun updateComments(
        userId: String,
        newUserName: String,
        newProfileImageUrl: String?,
        onComplete: (Boolean, String) -> Unit
    ) {
        databaseReference.child("blogs")
            .get()
            .addOnSuccessListener { snapshot ->
                for (blogSnapshot in snapshot.children) {
                    val commentsSnapshot = blogSnapshot.child("comments")
                    for (commentSnapshot in commentsSnapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        if (comment != null && comment.userId == userId) {
                            val commentRef = commentSnapshot.ref
                            commentRef.child("userName").setValue(newUserName)
                            if (newProfileImageUrl != null) {
                                commentRef.child("profileImage").setValue(newProfileImageUrl)
                            }
                        }
                    }
                }
                onComplete(true, "Comments updated successfully")
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Error updating comments")
            }
    }


    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}