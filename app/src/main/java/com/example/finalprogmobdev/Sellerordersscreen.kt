package com.example.finalprogmobdev

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class PickupStatus {
    PREPARING_ORDER,
    TRANSPORTING,
    READY_TO_PICKUP
}

data class CustomerOrder(
    val orderId: String,
    val customerName: String,
    val date: String,
    val items: List<OrderItem>,
    val total: Double,
    var pickupStatus: PickupStatus
)

@Composable
fun SellerOrdersScreen(
    onBackClick: () -> Unit
) {
    // Get real orders from database - use remember with key to recompose on changes
    var refreshTrigger by remember { mutableStateOf(0) }
    var orders by remember { mutableStateOf<List<CustomerOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Load ALL orders from Firebase (not just one user's)
    LaunchedEffect(refreshTrigger) {
        scope.launch {
            FirebaseManager.loadAllOrders().onSuccess { allOrders ->
                orders = allOrders
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
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2C2C2C)
                        )
                    }
                    Text(
                        text = "Order Management",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE31C3D))
            }
        } else if (orders.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = "No Orders",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No orders yet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C5C5C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Customer orders will appear here",
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F1E8))
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    SellerOrderCard(
                        order = order,
                        onStatusChange = { newStatus ->
                            order.pickupStatus = newStatus
                            // Save to Firebase
                            scope.launch {
                                FirebaseManager.updateOrderStatus(order.orderId, newStatus)
                            }
                            refreshTrigger++
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SellerOrderCard(
    order: CustomerOrder,
    onStatusChange: (PickupStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = order.orderId,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                    Text(
                        text = order.customerName,
                        fontSize = 14.sp,
                        color = Color(0xFF5C5C5C)
                    )
                    Text(
                        text = order.date,
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                // Status badge
                val statusColor = when (order.pickupStatus) {
                    PickupStatus.PREPARING_ORDER -> Color(0xFFFFA726)
                    PickupStatus.TRANSPORTING -> Color(0xFF42A5F5)
                    PickupStatus.READY_TO_PICKUP -> Color(0xFF66BB6A)
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = when (order.pickupStatus) {
                            PickupStatus.PREPARING_ORDER -> "Preparing"
                            PickupStatus.TRANSPORTING -> "Transporting"
                            PickupStatus.READY_TO_PICKUP -> "Ready"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Order items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C2C2C)
                        )
                        Text(
                            text = "Qty: ${item.quantity}",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "Php.%.2f".format(order.total),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE31C3D)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status update buttons
            Text(
                text = "Update Pickup Status:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C2C2C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusButton(
                    text = "Preparing",
                    isActive = order.pickupStatus == PickupStatus.PREPARING_ORDER,
                    color = Color(0xFFFFA726),
                    onClick = { onStatusChange(PickupStatus.PREPARING_ORDER) },
                    modifier = Modifier.weight(1f)
                )
                StatusButton(
                    text = "Transporting",
                    isActive = order.pickupStatus == PickupStatus.TRANSPORTING,
                    color = Color(0xFF42A5F5),
                    onClick = { onStatusChange(PickupStatus.TRANSPORTING) },
                    modifier = Modifier.weight(1f)
                )
                StatusButton(
                    text = "Ready",
                    isActive = order.pickupStatus == PickupStatus.READY_TO_PICKUP,
                    color = Color(0xFF66BB6A),
                    onClick = { onStatusChange(PickupStatus.READY_TO_PICKUP) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatusButton(
    text: String,
    isActive: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) color else Color(0xFFE0E0E0),
            contentColor = if (isActive) Color.White else Color(0xFF757575)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}