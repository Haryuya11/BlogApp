package com.example.blogapp.ui.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.blogapp.R
import com.example.blogapp.data.BlogItem
import com.example.blogapp.ui.viewmodel.UserBlogViewModel
import com.google.firebase.storage.FirebaseStorage
import com.skydoves.landscapist.glide.GlideImage

@SuppressLint("SimpleDateFormat")
@Composable
fun EditBlogScreen(
    navController: NavHostController,
    postId: String?,
    userBlogViewModel: UserBlogViewModel = viewModel()
) {
    val storage = FirebaseStorage.getInstance()
    var blogItem by remember { mutableStateOf<BlogItem?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var existingImageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        uris?.let {
            imageUris = it
        }
    }

    LaunchedEffect(postId) {
        postId?.let {
            blogItem = userBlogViewModel.fetchBlogPost(it)
            blogItem?.let { item ->
                existingImageUrls = item.imageBlog
            }
        }
    }

    blogItem?.let { item ->
        var title by remember { mutableStateOf(item.heading ?: "") }
        var content by remember { mutableStateOf(item.content ?: "") }

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.1f
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Blog",
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Blog Title",
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                val maxLength = 100
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= maxLength) {
                            title = it
                        }
                    },
                    label = { Text("Blog Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A3FF),
                        unfocusedBorderColor = Color(0xFF00A3FF),
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${title.length} / $maxLength",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium_italic)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                },
                label = { Text("Blog Description") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .width(180.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00A3FF), contentColor = Color.White
                    )
                ) {
                    Text("Add Images")
                }
                existingImageUrls.forEach { url ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        GlideImage(
                            imageModel = url,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                existingImageUrls = existingImageUrls.filter { it != url }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close, contentDescription = "Remove Image",
                                tint = Color.Red
                            )
                        }
                    }
                }
                imageUris.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        GlideImage(
                            imageModel = uri,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                imageUris = imageUris.filter { it != uri }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close, contentDescription = "Remove Image",
                                tint = Color.Red
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty() && content.isNotEmpty()) {
                            val updatedBlogItem = item.copy(
                                heading = title,
                                content = content,
                                imageBlog = existingImageUrls
                            )
                            userBlogViewModel.editBlogPost(
                                blogItem = updatedBlogItem,
                                imageUris = imageUris,
                                navController = navController,
                                existingImageUrls = existingImageUrls
                            )
                        } else {
                            Toast.makeText(
                                navController.context,
                                "Title and content cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00A3FF), contentColor = Color.White
                    )
                ) {
                    Text("Save Blog")
                }
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Blog not found", color = Color.Red, modifier = Modifier.padding(16.dp))
        }
    }
}