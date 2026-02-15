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

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToAllOrders: () -> Unit = {},
    onNavigateToTrackOrders: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Profile") }
    var notificationsEnabled by remember { mutableStateOf(false) }

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
                        .clickable { /* Edit profile */ }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile icon
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
                            text = "Example Student 1",
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

            // Orders Section
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

            // Others Section
            Text(
                text = "Others:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsMenuItem(
                icon = Icons.Default.Language,
                title = "Language",
                onClick = { /* Change language */ }
            )

            SettingsMenuItem(
                icon = Icons.Default.Logout,
                title = "Logout",
                onClick = onLogout,
                iconTint = Color(0xFFE31C3D),
                textColor = Color(0xFFE31C3D)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Version
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

