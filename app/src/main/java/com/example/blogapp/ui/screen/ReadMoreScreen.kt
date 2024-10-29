package com.example.blogapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.blogapp.R
import com.example.blogapp.data.BlogItem
import com.example.blogapp.data.Comment
import com.example.blogapp.ui.viewmodel.BlogViewModel
import com.skydoves.landscapist.glide.GlideImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReadMoreScreen(
    navController: NavHostController,
    postId: String?,
    blogViewModel: BlogViewModel = viewModel()
) {
    var blogItem by remember { mutableStateOf<BlogItem?>(null) }
    val comments = remember { mutableStateListOf<Comment>() }

    var newComment by remember { mutableStateOf("") }
    LaunchedEffect(postId) {
        postId?.let {
            blogItem = blogViewModel.fetchBlogPost(it)
            blogViewModel.getComments(it) { fetchedComments ->
                comments.clear()
                comments.addAll(fetchedComments)
            }
        }
    }

    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.1f),
        contentScale = ContentScale.Crop
    )

    blogItem?.let { item ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(50.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    )
                }
                Text(
                    text = "Read More",
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    GlideImage(
                        imageModel = item.profileImage ?: R.drawable.avatar_temp,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.navigate("userInfo/${item.userId}") },
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.userName ?: "New Blogger",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black),
                        fontFamily = FontFamily(Font(R.font.montserrat_light))
                    )
                    Text(
                        text = item.timestamp,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium_italic))
                    )
                }
            }
            Text(
                text = item.heading ?: "No Title",
                fontSize = 32.sp,
                color = Color(0xFF00A3FF),
                fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
                modifier = Modifier.padding(top = 32.dp, end = 32.dp)
            )

            Text(
                text = item.content ?: "No Content",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.montserrat_light)),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            item.imageBlog.forEach {
                Spacer(modifier = Modifier.height(16.dp))
                GlideImage(
                    imageModel = it,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Comments",
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.montserrat_semi_bold))
            )
            Spacer(modifier = Modifier.height(16.dp))
            comments.forEach {
                CommentItem(comment = it, navController = navController)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Add a comment") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            postId?.let {
                                blogViewModel.addComment(
                                    blogId = it,
                                    userId = blogViewModel.currentUser!!.uid,
                                    content = newComment,
                                    onCommentAdded = {
                                        blogViewModel.getComments(it) { fetchedComments ->
                                            comments.clear()
                                            comments.addAll(fetchedComments)
                                        }
                                    }
                                )
                                newComment = ""
                            }
                        }, enabled = newComment.isNotBlank()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            )
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Cannot load blog post", color = Color.Red)
        }
    }
}

@Composable
fun CommentItem(comment: Comment, navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.Top,
    ) {
        Card(
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier.size(40.dp)
        ) {
            GlideImage(
                imageModel = comment.profileImage,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { navController.navigate("userInfo/${comment.userId}") }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp)

        ) {
            Text(text = comment.userName)
            Text(text = comment.content)
            Text(text = comment.timestamp)
        }
    }
}