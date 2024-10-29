package com.example.blogapp.ui.viewmodel

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.blogapp.data.BlogItem
import com.example.blogapp.data.Comment
import com.example.blogapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlogViewModel : ViewModel() {
    private val _blogItems = MutableLiveData<List<BlogItem>>()
    val blogItems: LiveData<List<BlogItem>> = _blogItems
    private val databaseReference =
        FirebaseDatabase.getInstance("https://blogapp-43766-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    var currentUser = FirebaseAuth.getInstance().currentUser

    init {
        loadBlogs()
    }

    private fun loadBlogs() {
        databaseReference.child("blogs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogs = mutableListOf<BlogItem>()
                for (postSnapshot in snapshot.children) {
                    val blogItem = postSnapshot.getValue(BlogItem::class.java)
                    if (blogItem != null) {
                        loadUserProfileImageForBlog(blogItem)
                        loadIsSavedState(blogItem)
                        loadIsLikedState(blogItem)
                        blogs.add(blogItem)
                    }
                }
                blogs.reverse()
                _blogItems.value = blogs
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadIsLikedState(blogItem: BlogItem) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        userReference.child("likedBlogs").child(blogItem.blogId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    blogItem.isLiked = snapshot.exists()
                    updateBlogList(blogItem)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun loadIsSavedState(blogItem: BlogItem) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        userReference.child("savedBlogs").child(blogItem.blogId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    blogItem.isSaved = snapshot.exists()
                    updateBlogList(blogItem)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }


    private fun loadUserProfileImageForBlog(blogItem: BlogItem) {
        blogItem.userId?.let {
            databaseReference.child("users").child(it).child("profileImage")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val profileImage = snapshot.getValue(String::class.java)
                        blogItem.profileImage = profileImage
                        updateBlogList(blogItem)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun addBlog(
        userId: String,
        title: String,
        content: String,
        navController: NavHostController,
        imageBlog: List<String> = emptyList()
    ) {
        val userReference = databaseReference.child("users")
        userReference.child(userId).get().addOnSuccessListener { snapshot ->
            val userdata = snapshot.getValue(UserData::class.java)
            if (userdata != null) {
                val name = userdata.name
                val imageUrl = userdata.profileImage
                val currentTimestamp =
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val blogItem = BlogItem(
                    heading = title,
                    userName = name,
                    timestamp = currentTimestamp,
                    content = content,
                    userId = userId,
                    likedCount = 0,
                    savedCount = 0,
                    profileImage = imageUrl,
                    imageBlog = imageBlog
                )
                val key = databaseReference.push().key
                if (key != null) {
                    blogItem.blogId = key
                    databaseReference.child("blogs").child(key).setValue(blogItem)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    navController.context,
                                    "Blog added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    navController.context,
                                    "Failed to add blog: ${it.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    fun handleLikeButtonClick(
        postId: String,
        blogItem: BlogItem,
        isLiked: MutableState<Boolean>
    ) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        val postReference = databaseReference.child("blogs").child(postId).child("likes")
        viewModelScope.launch {
            postReference.child(currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            userReference.child("likedBlogs").child(postId).removeValue()
                                .addOnSuccessListener {
                                    postReference.child(currentUser!!.uid).removeValue()
                                    val newLikeCount = blogItem.likedCount - 1
                                    blogItem.likedCount = newLikeCount
                                    databaseReference.child("blogs").child(postId)
                                        .child("likedCount")
                                        .setValue(newLikeCount)
                                    blogItem.isLiked = false
                                    isLiked.value = false
                                }
                        } else {
                            userReference.child("likedBlogs").child(postId).setValue(true)
                                .addOnSuccessListener {
                                    postReference.child(currentUser!!.uid).setValue(true)
                                    val newLikeCount = blogItem.likedCount + 1
                                    blogItem.likedCount = newLikeCount
                                    databaseReference.child("blogs").child(postId)
                                        .child("likedCount")
                                        .setValue(newLikeCount)
                                    blogItem.isLiked = true
                                    isLiked.value = true
                                }
                        }
                        updateBlogList(blogItem)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    fun handleSaveButtonClick(
        postId: String,
        blogItem: BlogItem,
        isSaved: MutableState<Boolean>
    ) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        val postReference = databaseReference.child("blogs").child(postId).child("saves")
        viewModelScope.launch {
            postReference.child(currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            userReference.child("savedBlogs").child(postId).removeValue()
                                .addOnSuccessListener {
                                    postReference.child(currentUser!!.uid).removeValue()
                                    val newSaveCount = blogItem.savedCount - 1
                                    blogItem.savedCount = newSaveCount
                                    databaseReference.child("blogs").child(postId)
                                        .child("savedCount")
                                        .setValue(newSaveCount)
                                    blogItem.isSaved = false
                                    isSaved.value = false
                                }
                        } else {
                            userReference.child("savedBlogs").child(postId).setValue(true)
                                .addOnSuccessListener {
                                    postReference.child(currentUser!!.uid).setValue(true)
                                    val newSaveCount = blogItem.savedCount + 1
                                    blogItem.savedCount = newSaveCount
                                    databaseReference.child("blogs").child(postId)
                                        .child("savedCount")
                                        .setValue(newSaveCount)
                                    blogItem.isSaved = true
                                    isSaved.value = true
                                }
                        }
                        updateBlogList(blogItem)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    private fun updateBlogList(updatedBlogItem: BlogItem) {
        _blogItems.value = _blogItems.value?.map {
            if (it.blogId == updatedBlogItem.blogId) {
                updatedBlogItem
            } else {
                it
            }
        }
    }

    suspend fun fetchBlogPost(postId: String): BlogItem? {
        return try {
            val snapshot = databaseReference.child("blogs").child(postId).get().await()
            snapshot.getValue(BlogItem::class.java)?.apply {
                imageBlog =
                    snapshot.child("imageBlog").children.map {
                        it.getValue(String::class.java) ?: ""
                    }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getComments(blogId: String, onResult: (List<Comment>) -> Unit) {
        databaseReference.child("blogs").child(blogId).child("comments")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val comments = mutableListOf<Comment>()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        if (comment != null) {
                            comments.add(comment)
                        }
                    }
                    onResult(comments)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun addComment(
        blogId: String,
        userId: String,
        content: String,
        onCommentAdded: () -> Unit
    ) {
        val userReference = databaseReference.child("users").child(userId)
        userReference.get().addOnSuccessListener { snapshot ->
            val userdata = snapshot.getValue(UserData::class.java)
            if (userdata != null) {
                val userName = userdata.name
                val profileImage = userdata.profileImage
                val commentId =
                    databaseReference.child("blogs").child(blogId).child("comments").push().key
                        ?: return@addOnSuccessListener
                val currentTimestamp =
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                val comment = Comment(
                    commentId = commentId,
                    blogId = blogId,
                    userId = userId,
                    userName = userName,
                    profileImage = profileImage,
                    content = content,
                    timestamp = currentTimestamp
                )
                databaseReference.child("blogs").child(blogId).child("comments").child(commentId)
                    .setValue(comment).addOnCompleteListener {
                        if (it.isSuccessful) {
                            onCommentAdded()
                        }
                    }
            }
        }
    }
}