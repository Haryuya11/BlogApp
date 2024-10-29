package com.example.blogapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.blogapp.R
import com.example.blogapp.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel = viewModel()) {
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Blog",
                color = Color.Black,
                fontSize = 60.sp,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister))
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Share your story \n with the world",
                lineHeight = 40.sp,
                fontSize = 38.sp,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(100.dp))
            OutlinedTextField(
                value = loginEmail,
                onValueChange = { loginEmail = it },
                label = { Text("Email") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(300.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = loginPassword,
                onValueChange = { loginPassword = it },
                label = { Text("Password") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Red,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(300.dp),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            painter = if (showPassword) painterResource(id = R.drawable.visibility) else painterResource(
                                id = R.drawable.visibility_off
                            ),
                            contentDescription = null
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Forgot password?",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
                modifier = Modifier.clickable {
                    navController.navigate("forgotPassword")
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
            Button(
                onClick = {
                    if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
                        Toast.makeText(
                            navController.context,
                            "Please fill all the fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        userViewModel.login(loginEmail, loginPassword) { success, message ->
                            if (success) {
                                Toast.makeText(
                                    navController.context,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("main")
                            } else {
                                Toast.makeText(
                                    navController.context,
                                    "Login failed: $message",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, Color.Red)
            ) {
                Text(
                    text = "Login", color = Color.Black, fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Don't have an account?",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )
        }
    }
}

