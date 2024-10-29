package com.example.blogapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.R
import com.example.blogapp.ui.viewmodel.BlogViewModel
import com.example.blogapp.ui.viewmodel.UserViewModel
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    blogViewModel: BlogViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val blogItems by blogViewModel.blogItems.observeAsState(emptyList())
    val user by remember { mutableStateOf(blogViewModel.currentUser) }
    var userName by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.uid?.let { userId ->
            userViewModel.loadUserProfile(userId) { name, image ->
                userName = name
                profileImage = image
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredBlogItems = blogItems.filter {
        it.heading?.contains(searchQuery, ignoreCase = true) == true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "New feeds",
                    modifier = Modifier.padding(start = 16.dp),
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.montserrat_alternates_semi_bold)),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.navigate("savedBlogs") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.unsaved_blog_black),
                            contentDescription = "Saved Blog"
                        )
                    }
                    Card(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { navController.navigate("profile") },
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        GlideImage(
                            imageModel = profileImage.ifEmpty { R.drawable.avatar_temp },

                            contentScale = ContentScale.Crop,
                            requestOptions = {
                                RequestOptions().circleCrop()
                            },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* Handle search action if needed */ },
                active = false,
                onActiveChange = { /* Handle active state change if needed */ },
                placeholder = { Text("Search...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                content = { /* Add content here if needed */ }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(filteredBlogItems, key = { it.blogId }) { blogItem ->
                    BlogItemView(
                        blogItem = blogItem,
                        blogViewModel = blogViewModel,
                        navController = navController
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("addBlog") },
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd),
            shape = RoundedCornerShape(100.dp),
            containerColor = Color(0xFF00A3FF)
        ) {
            Icon(
                Icons.Default.Add, contentDescription = "Add Blog",
                tint = Color.White
            )
        }
    }
}