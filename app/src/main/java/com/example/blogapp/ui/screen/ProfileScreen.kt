package com.example.blogapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.blogapp.ui.viewmodel.BlogViewModel
import com.example.blogapp.ui.viewmodel.UserViewModel
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileScreen(
    navController: NavHostController,
    blogViewModel: BlogViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E4E4E))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Card(
                modifier = Modifier
                    .size(200.dp),
                shape = RoundedCornerShape(400.dp)
            ) {
                GlideImage(
                    imageModel = profileImage.ifEmpty { R.drawable.avatar_temp },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = userName,
                fontSize = 24.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.montserrat_semi_bold)),
                modifier = Modifier.padding(top = 24.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .width(234.dp)
                    .padding(top = 8.dp),
                thickness = 1.dp,
                color = Color.White
            )

            Button(
                onClick = { navController.navigate("yourBlogs") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Text(
                    text = "Your Blogs",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_semi_bold))
                )
            }

            Button(
                onClick = { navController.navigate("userInfo/${user?.uid}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Text(
                    text = "User Info",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_semi_bold))
                )
            }

            Button(
                onClick = { navController.navigate("changePassword") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Text(
                    text = "Change Password",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_semi_bold))
                )
            }

            Button(
                onClick = {
                    userViewModel.signOut()
                    navController.navigate("login")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Text(
                    text = "Log Out",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.montserrat_semi_bold))
                )
            }
        }
    }
}