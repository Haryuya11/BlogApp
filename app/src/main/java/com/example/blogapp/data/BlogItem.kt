package com.example.blogapp.data

data class BlogItem(
    var heading: String? = "",
    val userName: String? = "",
    val timestamp: String = "",
    val userId: String? = "",
    var content: String? = "",
    var profileImage: String? = "",
    var blogId: String = "",
    var likedCount: Int = 0,
    var savedCount: Int = 0,
    var isSaved: Boolean = false,
    var isLiked: Boolean = false,
    var imageBlog: List<String> = emptyList()
)