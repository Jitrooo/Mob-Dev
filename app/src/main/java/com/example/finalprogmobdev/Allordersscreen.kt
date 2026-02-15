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
import java.text.SimpleDateFormat
import java.util.*

data class Order(
    val orderId: String,
    val date: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: OrderStatus
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageRes: Int
)

enum class OrderStatus {
    PROCESSING,
    SHIPPING,
    DELIVERED,
    CANCELLED
}

@Composable
fun AllOrdersScreen(
    onBackClick: () -> Unit
) {
    // Sample orders - in real app, fetch from database/API
    val orders = remember {
        listOf(
            Order(
                orderId = "#ORD-2024-001",
                date = "Feb 4, 2024",
                items = listOf(
                    OrderItem("SHS Uniform", 1, 800.0, R.drawable.mapua_logo),
                    OrderItem("ID Sling", 2, 100.0, R.drawable.mapua_logo)
                ),
                total = 1000.0,
                status = OrderStatus.SHIPPING
            ),
            Order(
                orderId = "#ORD-2024-002",
                date = "Feb 1, 2024",
                items = listOf(
                    OrderItem("MMCM Jacket", 1, 1500.0, R.drawable.mapua_logo)
                ),
                total = 1500.0,
                status = OrderStatus.DELIVERED
            ),
            Order(
                orderId = "#ORD-2024-003",
                date = "Jan 28, 2024",
                items = listOf(
                    OrderItem("Long Booklet", 5, 10.0, R.drawable.mapua_logo)
                ),
                total = 50.0,
                status = OrderStatus.DELIVERED
            )
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
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2C2C2C)
                        )
                    }
                    Text(
                        text = "All Orders",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F1E8))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderCard(order = order)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderId,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                    Text(
                        text = order.date,
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                // Status badge
                val statusColor = when (order.status) {
                    OrderStatus.PROCESSING -> Color(0xFFFFA726)
                    OrderStatus.SHIPPING -> Color(0xFF42A5F5)
                    OrderStatus.DELIVERED -> Color(0xFF66BB6A)
                    OrderStatus.CANCELLED -> Color(0xFFEF5350)
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = order.status.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFE0E0E0)
            )

            // Order items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(50.dp)
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

                    Text(
                        text = "Php.%.2f".format(item.price * item.quantity),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C2C2C)
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFE0E0E0)
            )

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
        }
    }
}

