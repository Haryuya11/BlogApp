package com.example.blogapp.ui.viewmodel

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.blogapp.data.BlogItem
import com.example.blogapp.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class UserBlogViewModel : ViewModel() {
    private val _blogItems = MutableStateFlow<List<BlogItem>>(emptyList())
    val blogItems: StateFlow<List<BlogItem>> = _blogItems

    private val auth = FirebaseAuth.getInstance()
    private val databaseReference =
        FirebaseDatabase.getInstance("https://blogapp-43766-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("blogs")
    private val storage = FirebaseStorage.getInstance()

    init {
        fetchUserBlog()
    }

    private fun fetchUserBlog() {
        val currentUserID = auth.currentUser?.uid
        if (currentUserID != null) {
            databaseReference.orderByChild("userId").equalTo(currentUserID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val blogList = ArrayList<BlogItem>()
                        for (postSnapshot in snapshot.children) {
                            val blogItem = postSnapshot.getValue(BlogItem::class.java)
                            if (blogItem != null) {
                                blogList.add(blogItem)
                            }
                        }
                        _blogItems.value = blogList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    fun deleteBlogPost(blogItem: BlogItem) {
        val postId = blogItem.blogId
        databaseReference.child(postId).removeValue()
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    suspend fun fetchBlogPost(postId: String): BlogItem? {
        val blogItem =
            databaseReference.child(postId).get().await().getValue(BlogItem::class.java)
        return blogItem
    }

    fun editBlogPost(
        blogItem: BlogItem,
        imageUris: List<Uri>,
        navController: NavHostController,
        existingImageUrls: List<String>
    ) {
        val postId = blogItem.blogId
        if (imageUris.isNotEmpty()) {
            val imageUrls = existingImageUrls.toMutableList()
            imageUris.forEach { uri ->
                val imageRef = storage.reference.child("blog_images/${UUID.randomUUID()}")
                imageRef.putFile(uri).addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        imageUrls.add(url.toString())
                        if (imageUrls.size == imageUris.size + existingImageUrls.size) {
                            blogItem.imageBlog = imageUrls
                            updateBlogPost(blogItem, navController)
                        }
                    }
                }
            }
        } else {
            blogItem.imageBlog = existingImageUrls
            updateBlogPost(blogItem, navController)
        }
    }

    private fun updateBlogPost(blogItem: BlogItem, navController: NavHostController) {
        val postId = blogItem.blogId
        databaseReference.child(postId).get().addOnSuccessListener { snapshot ->
            val existingBlogItem = snapshot.getValue(BlogItem::class.java)
            existingBlogItem?.let {
                val comments =
                    snapshot.child("comments").children.mapNotNull { it.getValue(Comment::class.java) }
                databaseReference.child(postId).setValue(blogItem).addOnCompleteListener {
                    databaseReference.child(postId).child("comments").setValue(comments)
                        .addOnCompleteListener {
                            Toast.makeText(
                                navController.context,
                                "Blog updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }
                }.addOnFailureListener {
                    Toast.makeText(
                        navController.context,
                        "Failed to update blog: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}