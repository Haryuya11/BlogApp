package com.example.blogapp.data

data class Comment(
    val commentId: String = "",
    val blogId: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val profileImage: String = "",
    val timestamp: String = "",
)