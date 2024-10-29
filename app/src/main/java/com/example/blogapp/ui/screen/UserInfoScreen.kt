package com.example.blogapp.ui.screen

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blogapp.R
import com.example.blogapp.ui.viewmodel.UserViewModel
import com.skydoves.landscapist.glide.GlideImage
import java.util.Calendar

@Composable
fun UserInfoScreen(
    userId: String?,
    userViewModel: UserViewModel = viewModel()
) {
    var userName by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf<String?>(null) }
    var gender by remember { mutableStateOf<String?>(null) }
    var hobbies by remember { mutableStateOf<String?>(null) }
    var country by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    var oldUserName by remember { mutableStateOf("") }
    var oldProfileImage by remember { mutableStateOf("") }
    var oldDob by remember { mutableStateOf<String?>(null) }
    var oldGender by remember { mutableStateOf<String?>(null) }
    var oldHobbies by remember { mutableStateOf<String?>(null) }
    var oldCountry by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.loadUserInfo(it) { name, image, userEmail, userDob, userGender, userHobbies, userCountry ->
                userName = name
                profileImage = image
                email = userEmail
                dob = userDob
                gender = userGender
                hobbies = userHobbies
                country = userCountry

                oldUserName = name
                oldProfileImage = image
                oldDob = userDob
                oldGender = userGender
                oldHobbies = userHobbies
                oldCountry = userCountry
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileImage = it.toString()
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
            Spacer(modifier = Modifier.height(30.dp))
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .clickable { if (isEditing) imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(400.dp)
            ) {
                GlideImage(
                    imageModel = profileImage.ifEmpty { R.drawable.avatar_temp },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (isEditing) {
                val calendar = Calendar.getInstance()
                if (dob == null || dob == "Unknown") {
                    dob =
                        "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                            calendar.get(Calendar.YEAR)
                        }"
                }
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        dob = "$dayOfMonth/${month + 1}/$year"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Username", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "D.O.B: ${dob ?: "Unknown"}",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                        .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                val genderOptions = listOf("Male", "Female", "Other")

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gender: ${gender ?: "Unknown"}",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                            .padding(16.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hobbies ?: "",
                    onValueChange = { hobbies = it },
                    label = { Text("Hobbies", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = country ?: "",
                    onValueChange = { country = it },
                    label = { Text("Country", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (userName.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Username cannot be empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {

                                userId?.let { userId ->
                                    val profileImageUri =
                                        if (profileImage.isNotEmpty() && profileImage != oldProfileImage) Uri.parse(
                                            profileImage
                                        ) else null
                                    hobbies =
                                        if (hobbies.isNullOrBlank()) null else hobbies
                                    country =
                                        if (country.isNullOrBlank()) null else country
                                    userViewModel.updateUserData(
                                        userId,
                                        userName,
                                        profileImageUri,
                                        dob,
                                        gender,
                                        hobbies,
                                        country
                                    ) { success, message ->
                                        if (success) {
                                            userViewModel.updateUserDetailsInBlogs(
                                                userId,
                                                userName,
                                                profileImageUri
                                            ) { blogUpdateSuccess, blogUpdateMessage ->
                                                if (blogUpdateSuccess) {
                                                    userViewModel.updateUserDetailsInComments(
                                                        userId,
                                                        userName,
                                                        profileImageUri
                                                    ) { commentUpdateSuccess, commentUpdateMessage ->
                                                        if (commentUpdateSuccess) {
                                                            isEditing = false
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                commentUpdateMessage,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "Save",
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            userName = oldUserName
                            profileImage = oldProfileImage
                            dob = oldDob
                            gender = oldGender
                            hobbies = oldHobbies
                            country = oldCountry
                            isEditing = false
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "Cancel",
                        )
                    }
                }
            } else {
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "D.O.B: $dob",
                    fontSize = 18.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Gender: $gender",
                    fontSize = 18.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Hobbies: ${hobbies ?: "Unknown"}",
                    fontSize = 18.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Country: ${country ?: "Unknown"}",
                    fontSize = 18.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (userId == userViewModel.auth.currentUser?.uid) {
                    Button(
                        onClick = {
                            oldUserName = userName
                            oldProfileImage = profileImage
                            oldDob = dob
                            oldGender = gender
                            oldHobbies = hobbies
                            oldCountry = country
                            isEditing = true
                        },
                        modifier = Modifier
                            .width(300.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}