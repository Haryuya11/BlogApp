package com.example.blogapp.ui.screen

import android.net.Uri
import android.util.Log
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
import com.example.blogapp.ui.viewmodel.BlogViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.skydoves.landscapist.glide.GlideImage
import java.util.UUID

@Composable
fun AddBlogScreen(navController: NavHostController, blogViewModel: BlogViewModel = viewModel()) {
    val auth = FirebaseAuth.getInstance()
    val storage = FirebaseStorage.getInstance()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImageUris = uris
    }

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
                modifier = Modifier
                    .size(50.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Blog",
                fontSize = 24.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
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
                    unfocusedBorderColor = Color(0xFF00A3FF)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${title.length}/$maxLength",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Blog Description",
            fontSize = 24.sp,
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
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

        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00A3FF),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Add Images")
            }
            selectedImageUris.forEach { uri ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    IconButton(
                        onClick = {
                            selectedImageUris = selectedImageUris.filter { it != uri }
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
                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        val user = auth.currentUser
                        if (user != null) {
                            val userId = user.uid
                            if (selectedImageUris.isNotEmpty()) {
                                val imagesUrls = mutableListOf<String>()
                                selectedImageUris.forEach { uri ->
                                    val imageRef =
                                        storage.reference.child("blog_images/${UUID.randomUUID()}")
                                    imageRef.putFile(uri)
                                        .addOnSuccessListener {
                                            imageRef.downloadUrl.addOnSuccessListener { url ->
                                                imagesUrls.add(url.toString())
                                                if (imagesUrls.size == selectedImageUris.size) {
                                                    blogViewModel.addBlog(
                                                        userId = userId,
                                                        title = title,
                                                        content = description,
                                                        imageBlog = imagesUrls,
                                                        navController = navController
                                                    )
                                                }
                                            }.addOnFailureListener { e ->
                                                Log.e(
                                                    "AddBlogScreen",
                                                    "Failed to get download URL",
                                                    e
                                                )
                                            }
                                        }.addOnFailureListener { e ->
                                            Log.e("AddBlogScreen", "Failed to upload image", e)
                                        }
                                }
                            } else {
                                blogViewModel.addBlog(
                                    userId = userId,
                                    title = title,
                                    content = description,
                                    imageBlog = emptyList(),
                                    navController = navController
                                )
                            }
                        } else {
                            Log.e("AddBlogScreen", "User is null")
                        }
                    } else {
                        Log.e("AddBlogScreen", "Title or description is empty")
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00A3FF),
                    contentColor = Color.White
                )
            ) {
                Text("Add Blog")
            }
        }
    }
}