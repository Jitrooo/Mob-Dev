package com.example.finalprogmobdev

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SellerDashboardScreen(
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onLogout: () -> Unit
) {
    var totalOrders by remember { mutableStateOf(0) }
    var pendingOrders by remember { mutableStateOf(0) }
    var totalRevenue by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Load real data from Firebase
    LaunchedEffect(Unit) {
        scope.launch {
            FirebaseManager.loadAllOrders().onSuccess { orders ->
                totalOrders = orders.size
                pendingOrders = orders.count {
                    it.pickupStatus != PickupStatus.READY_TO_PICKUP
                }
                totalRevenue = orders.sumOf { it.total }
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE31C3D),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Seller Dashboard",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Admin Panel",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F1E8))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Manage your bookstore",
                fontSize = 16.sp,
                color = Color(0xFF5C5C5C),
                modifier = Modifier.padding(16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE31C3D))
                }
            } else {
                // Statistics cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Orders",
                        value = totalOrders.toString(),
                        backgroundColor = Color(0xFF42A5F5),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Pending",
                        value = pendingOrders.toString(),
                        backgroundColor = Color(0xFFFFA726),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Revenue card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF66BB6A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Total Revenue",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "Php.%.2f".format(totalRevenue),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Management cards
            ManagementCard(
                icon = Icons.Default.Inventory,
                title = "Product Inventory",
                description = "Manage stock, add/remove products",
                backgroundColor = Color(0xFF66BB6A),
                onClick = onNavigateToInventory
            )

            Spacer(modifier = Modifier.height(12.dp))

            ManagementCard(
                icon = Icons.Default.ShoppingBag,
                title = "Order Management",
                description = "View and update order status",
                backgroundColor = Color(0xFF42A5F5),
                onClick = onNavigateToOrders
            )

            Spacer(modifier = Modifier.height(12.dp))

            ManagementCard(
                icon = Icons.Default.Receipt,
                title = "Transaction History",
                description = "View all sales and receipts",
                backgroundColor = Color(0xFFFFA726),
                onClick = onNavigateToTransactions
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun ManagementCard(
    icon: ImageVector,
    title: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color(0xFF9E9E9E)
            )
        }
    }
}