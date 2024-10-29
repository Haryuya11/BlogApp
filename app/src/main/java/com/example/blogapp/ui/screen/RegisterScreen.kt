package com.example.blogapp.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.R
import com.example.blogapp.ui.viewmodel.UserViewModel
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun RegisterScreen(navController: NavHostController, userViewModel: UserViewModel = viewModel()) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

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
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .size(64.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(200.dp),
            ) {
                imageUri?.let {
                    GlideImage(
                        imageModel = it,
                        requestOptions = {
                            RequestOptions().circleCrop()
                        },
                        modifier = Modifier.size(64.dp)
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.avatar_temp),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = registerEmail,
                onValueChange = { registerEmail = it },
                label = { Text("Enter your email") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Blue,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(300.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = registerName,
                onValueChange = { registerName = it },
                label = { Text("Enter your name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Blue,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(300.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = registerPassword,
                onValueChange = { registerPassword = it },
                label = { Text("Enter your password") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Blue,
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
                            painter = if (showPassword) {
                                painterResource(id = R.drawable.visibility)
                            } else {
                                painterResource(id = R.drawable.visibility_off)
                            },
                            contentDescription = null
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm your password") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Blue,
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
                            painter = if (showPassword) {
                                painterResource(id = R.drawable.visibility)
                            } else {
                                painterResource(id = R.drawable.visibility_off)
                            },
                            contentDescription = null
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = {
                    if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(
                            navController.context,
                            "Please fill all the fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (imageUri == null) {
                        Toast.makeText(
                            navController.context,
                            "Please select an image",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (registerPassword == confirmPassword) {
                            userViewModel.register(
                                registerName,
                                registerEmail,
                                registerPassword,
                                imageUri
                            ) { success, message ->
                                if (success) {
                                    Toast.makeText(
                                        navController.context,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login")
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Registration failed: $message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                navController.context,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, Color.Blue)
            ) {
                Text(
                    text = "Register", color = Color.Black, fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Already have an account?",
                fontSize = 20.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.love_ya_like_a_sister)),
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
    }
}