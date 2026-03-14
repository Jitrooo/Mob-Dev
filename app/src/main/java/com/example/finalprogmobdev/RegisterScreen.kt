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
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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
            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Let's Register",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Do you have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF5C5C5C)
                )
                Text(
                    text = "login",
                    fontSize = 14.sp,
                    color = Color(0xFFE31C3D),
                    modifier = Modifier.clickable { onBackClick() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error message
            if (showError) {
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFE31C3D),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // First Name field
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    showError = false
                },
                label = { Text("First Name") },
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
                isError = showError && firstName.isEmpty(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last Name field
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    showError = false
                },
                label = { Text("Last Name") },
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
                isError = showError && lastName.isEmpty(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    showError = false
                },
                label = { Text("Email") },
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = showError && email.isEmpty(),
                singleLine = true,
                enabled = !isLoading
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
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register button
            Button(
                onClick = {
                    // Validation
                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Please fill in all the boxes"
                        showError = true
                    } else if (!email.contains("@")) {
                        errorMessage = "Please enter a valid email"
                        showError = true
                    } else if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        showError = true
                    } else {
                        // Register with Firebase
                        isLoading = true
                        scope.launch {
                            val result = FirebaseManager.registerUser(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password
                            )

                            isLoading = false

                            result.onSuccess {
                                onRegisterSuccess()
                            }.onFailure { error ->
                                errorMessage = when {
                                    error.message?.contains("already in use") == true ->
                                        "Email already registered"
                                    error.message?.contains("invalid-email") == true ->
                                        "Invalid email format"
                                    error.message?.contains("weak-password") == true ->
                                        "Password is too weak"
                                    else -> error.message ?: "Registration failed"
                                }
                                showError = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE31C3D)
                ),
                shape = MaterialTheme.shapes.small,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "REGISTER",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}