package com.example.finalprogmobdev

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Global user profile storage
object UserProfile {
    var studentName by mutableStateOf("Example Student 1")
}

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToAllOrders: () -> Unit = {},
    onNavigateToTrackOrders: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Profile") }
    var notificationsEnabled by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    // Load user data from Firebase
    LaunchedEffect(Unit) {
        val userId = FirebaseManager.getCurrentUserId()
        if (userId != null) {
            scope.launch {
                FirebaseManager.getUserData(userId).onSuccess { userData ->
                    val firstName = userData["firstName"] as? String ?: "Student"
                    val lastName = userData["lastName"] as? String ?: ""
                    UserProfile.studentName = "$firstName $lastName".trim()
                    isLoading = false
                }.onFailure {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }
    }

    if (showEditDialog) {
        EditNameDialog(
            currentName = UserProfile.studentName,
            onDismiss = { showEditDialog = false },
            onSave = { newName ->
                UserProfile.studentName = newName
                showEditDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutUsDialog(
            onDismiss = { showAboutDialog = false },
            onBackToHome = {
                showAboutDialog = false
                onBackClick()
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == "Home",
                    onClick = {
                        selectedTab = "Home"
                        onBackClick()
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE31C3D),
                        selectedTextColor = Color(0xFFE31C3D),
                        indicatorColor = Color(0xFFFFE5E5)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = selectedTab == "Search",
                    onClick = { selectedTab = "Search" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE31C3D),
                        selectedTextColor = Color(0xFFE31C3D),
                        indicatorColor = Color(0xFFFFE5E5)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                    label = { Text("Cart") },
                    selected = selectedTab == "Cart",
                    onClick = { selectedTab = "Cart" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE31C3D),
                        selectedTextColor = Color(0xFFE31C3D),
                        indicatorColor = Color(0xFFFFE5E5)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == "Profile",
                    onClick = { selectedTab = "Profile" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE31C3D),
                        selectedTextColor = Color(0xFFE31C3D),
                        indicatorColor = Color(0xFFFFE5E5)
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F1E8))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEditDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE31C3D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = UserProfile.studentName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                        Text(
                            text = "Edit Personal Details",
                            fontSize = 14.sp,
                            color = Color(0xFF5C5C5C)
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Edit",
                        tint = Color(0xFF5C5C5C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Orders:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsMenuItem(
                icon = Icons.Default.ShoppingCart,
                title = "All Orders",
                onClick = onNavigateToAllOrders
            )

            SettingsMenuItem(
                icon = Icons.Default.LocalShipping,
                title = "Track Orders",
                onClick = onNavigateToTrackOrders
            )

            SettingsMenuItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                hasSwitch = true,
                switchChecked = notificationsEnabled,
                onSwitchChange = { notificationsEnabled = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Others:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsMenuItem(
                icon = Icons.Default.Info,
                title = "About Us",
                onClick = { showAboutDialog = true }
            )

            SettingsMenuItem(
                icon = Icons.Default.Logout,
                title = "Logout",
                onClick = onLogout,
                iconTint = Color(0xFFE31C3D),
                textColor = Color(0xFFE31C3D)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Version 1.0",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    hasSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    iconTint: Color = Color(0xFF2C2C2C),
    textColor: Color = Color(0xFF2C2C2C)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !hasSwitch, onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            if (hasSwitch) {
                Switch(
                    checked = switchChecked,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFE31C3D),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFF6B7280)
                    )
                )
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parts = name.trim().split(" ", limit = 2)
                    val firstName = parts.getOrNull(0) ?: ""
                    val lastName = parts.getOrNull(1) ?: ""

                    if (firstName.isNotEmpty()) {
                        isLoading = true
                        scope.launch {
                            val userId = FirebaseManager.getCurrentUserId()
                            if (userId != null) {
                                FirebaseManager.updateUserName(userId, firstName, lastName).onSuccess {
                                    UserProfile.studentName = name
                                    onSave(name)
                                    isLoading = false
                                }.onFailure {
                                    isLoading = false
                                }
                            }
                        }
                    }
                },
                enabled = name.isNotBlank() && !isLoading
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AboutUsDialog(
    onDismiss: () -> Unit,
    onBackToHome: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "About Us",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "MMCM Bookstore App",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE31C3D)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "An E-commerce application designed to bring MMCM school supplies to the comfort of your home. " +
                            "Purchase booklets, school materials, uniforms, and MMCM merchandise with ease. " +
                            "You'll receive in-app notifications when your order is ready for pickup at the MMCM Bookstore.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Developed By:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Mhar Jethro A. Zacal\n  3rd Year Computer Engineering\n",
                    fontSize = 14.sp
                )
                Text(
                    text = "• Lance Matthew S. Babano\n  3rd Year Electronics Engineering\n",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Section A361",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "In partial fulfillment of the requirements for CPE144L Mobile Development (Laboratory)",
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFF5C5C5C)
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
                Button(
                    onClick = onBackToHome,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE31C3D)
                    )
                ) {
                    Text("Back to Home")
                }
            }
        },
        dismissButton = {}
    )
}