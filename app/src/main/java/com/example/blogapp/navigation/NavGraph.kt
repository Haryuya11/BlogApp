package com.example.blogapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.blogapp.ui.screen.AddBlogScreen
import com.example.blogapp.ui.screen.ChangePasswordScreen
import com.example.blogapp.ui.screen.EditBlogScreen
import com.example.blogapp.ui.screen.ForgotPasswordScreen
import com.example.blogapp.ui.screen.LoginScreen
import com.example.blogapp.ui.screen.MainScreen
import com.example.blogapp.ui.screen.ProfileScreen
import com.example.blogapp.ui.screen.ReadMoreScreen
import com.example.blogapp.ui.screen.RegisterScreen
import com.example.blogapp.ui.screen.SavedBlogsScreen
import com.example.blogapp.ui.screen.SplashScreen
import com.example.blogapp.ui.screen.UserInfoScreen
import com.example.blogapp.ui.screen.YourBlogScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController = navController) }
        composable("main") { MainScreen(navController = navController) }
        composable("addBlog") { AddBlogScreen(navController = navController) }
        composable("savedBlogs") { SavedBlogsScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }
        composable("yourBlogs") { YourBlogScreen(navController = navController) }
        composable("readMore/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            ReadMoreScreen(navController = navController, postId = postId)
        }
        composable("editBlog/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            EditBlogScreen(navController = navController, postId = postId)
        }
        composable("userInfo/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            UserInfoScreen(userId = userId)
        }
        composable("changePassword") { ChangePasswordScreen(navController = navController) }
    }
}

