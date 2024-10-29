package com.example.blogapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.blogapp.R
import com.example.blogapp.data.BlogItem
import com.example.blogapp.ui.viewmodel.BlogViewModel
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun BlogItemView(
    blogItem: BlogItem,
    blogViewModel: BlogViewModel = viewModel(),
    navController: NavHostController
) {
    val isLiked = remember { mutableStateOf(blogItem.isLiked) }
    val isSaved = remember { mutableStateOf(blogItem.isSaved) }
    LaunchedEffect(blogItem.isLiked) {
        isLiked.value = blogItem.isLiked
    }
    LaunchedEffect(blogItem.isSaved) {
        isSaved.value = blogItem.isSaved
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = blogItem.heading ?: "No Title",
                    fontSize = 20.sp,
                    modifier = Modifier.width(230.dp),
                    color = Color(0xFF00A3FF),
                    fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
                    maxLines = 2,
                )
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    GlideImage(
                        imageModel = blogItem.profileImage ?: R.drawable.avatar_temp,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.navigate("userInfo/${blogItem.userId}") },
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = blogItem.userName ?: "New Blogger",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.montserrat_light)),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = blogItem.timestamp,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.montserrat_medium_italic)),
                    )

                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = blogItem.content ?: "No Content",
                fontSize = 14.sp,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = FontFamily(Font(R.font.montserrat_light)),
                letterSpacing = 0.03.sp,
                lineHeight = 22.sp,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("readMore/${blogItem.blogId}") },
                    Modifier
                        .width(180.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00A3FF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Read More")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = blogItem.likedCount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = {
                        blogViewModel.handleLikeButtonClick(
                            blogItem.blogId,
                            blogItem,
                            isLiked
                        )
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (isLiked.value) {
                                    R.drawable.heart_fill_red
                                } else {
                                    R.drawable.heart_black
                                }
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = {
                        blogViewModel.handleSaveButtonClick(
                            blogItem.blogId,
                            blogItem,
                            isSaved
                        )
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (isSaved.value) {
                                    R.drawable.save_blog_fll_red
                                } else {
                                    R.drawable.unsaved_blog_black
                                }
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}

