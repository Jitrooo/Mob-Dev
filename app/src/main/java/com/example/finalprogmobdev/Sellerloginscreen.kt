package com.example.finalprogmobdev

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SellerLoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Simple hardcoded credentials for demo (in real app, use database)
    val SELLER_USERNAME = "admin"
    val SELLER_PASSWORD = "admin123"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo and Bookstore text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mapua_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BOOKSTORE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC41E3A)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Seller Login title
            Text(
                text = "Seller Login",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Admin Dashboard Access",
                fontSize = 14.sp,
                color = Color(0xFF5C5C5C)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Error message
            if (showError) {
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFE31C3D),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    showError = false
                },
                label = { Text("Username") },
                modifier = Modifier
                    .width(280.dp)
                    .height(60.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFE31C3D),
                    errorBorderColor = Color(0xFFE31C3D)
                ),
                isError = showError && username.isEmpty(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field with show/hide
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    showError = false
                },
                label = { Text("Password") },
                modifier = Modifier
                    .width(280.dp)
                    .height(60.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFE31C3D),
                    errorBorderColor = Color(0xFFE31C3D)
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = showError && password.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    if (username.isEmpty() || password.isEmpty()) {
                        errorMessage = "Please fill in all the boxes"
                        showError = true
                    } else if (username == SELLER_USERNAME && password == SELLER_PASSWORD) {
                        onLoginSuccess()
                    } else {
                        errorMessage = "Invalid username or password"
                        showError = true
                    }
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE31C3D)
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "LOGIN AS SELLER",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back to customer login
            Text(
                text = "Back to Customer Login",
                fontSize = 14.sp,
                color = Color(0xFFE31C3D),
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}