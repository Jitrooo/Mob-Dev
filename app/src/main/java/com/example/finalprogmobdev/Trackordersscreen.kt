package com.example.finalprogmobdev

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class DeliveryOrder(
    val orderId: String,
    val date: String,
    val items: List<OrderItem>,
    val total: Double,
    val deliveryStatus: DeliveryStatus,
    var isReceived: Boolean = false
)

enum class DeliveryStatus {
    ORDER_PLACED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED
}

@Composable
fun TrackOrdersScreen(
    onBackClick: () -> Unit
) {
    // Sample delivery orders
    var orders by remember {
        mutableStateOf(listOf(
            DeliveryOrder(
                orderId = "#ORD-2024-001",
                date = "Feb 4, 2024",
                items = listOf(
                    OrderItem("SHS Uniform", 1, 800.0, R.drawable.mapua_logo),
                    OrderItem("ID Sling", 2, 100.0, R.drawable.mapua_logo)
                ),
                total = 1000.0,
                deliveryStatus = DeliveryStatus.OUT_FOR_DELIVERY
            ),
            DeliveryOrder(
                orderId = "#ORD-2024-002",
                date = "Feb 1, 2024",
                items = listOf(
                    OrderItem("MMCM Jacket", 1, 1500.0, R.drawable.mapua_logo)
                ),
                total = 1500.0,
                deliveryStatus = DeliveryStatus.DELIVERED,
                isReceived = false
            )
        ))
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
                        text = "Track Orders",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(orders) { order ->
                TrackingCard(
                    order = order,
                    onOrderReceived = {
                        orders = orders.map {
                            if (it.orderId == order.orderId) it.copy(isReceived = true)
                            else it
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TrackingCard(
    order: DeliveryOrder,
    onOrderReceived: () -> Unit
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
                        text = order.date,
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Delivery status timeline
            DeliveryTimeline(status = order.deliveryStatus)

            Spacer(modifier = Modifier.height(20.dp))

            // Current status text
            val statusText = when (order.deliveryStatus) {
                DeliveryStatus.ORDER_PLACED -> "Your order has been placed"
                DeliveryStatus.PREPARING -> "We're preparing your order"
                DeliveryStatus.OUT_FOR_DELIVERY -> "Your order is out for delivery"
                DeliveryStatus.DELIVERED -> if (order.isReceived) "Order completed" else "Your order has been delivered"
            }

            Surface(
                color = Color(0xFF42A5F5).copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color(0xFF42A5F5),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        color = Color(0xFF2C2C2C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(12.dp))

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

            // Order Received button (only show if delivered and not received)
            if (order.deliveryStatus == DeliveryStatus.DELIVERED && !order.isReceived) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOrderReceived,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66BB6A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ORDER RECEIVED",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Show completion badge if received
            if (order.isReceived) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color(0xFF66BB6A).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF66BB6A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Order Completed",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF66BB6A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryTimeline(status: DeliveryStatus) {
    val steps = listOf(
        "Order Placed" to DeliveryStatus.ORDER_PLACED,
        "Preparing" to DeliveryStatus.PREPARING,
        "Out for Delivery" to DeliveryStatus.OUT_FOR_DELIVERY,
        "Delivered" to DeliveryStatus.DELIVERED
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (label, stepStatus) ->
            val isCompleted = stepStatus.ordinal <= status.ordinal

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Status circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) Color(0xFF66BB6A)
                            else Color(0xFFE0E0E0)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Label
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (isCompleted) Color(0xFF2C2C2C) else Color(0xFF9E9E9E),
                    fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            // Connecting line (except for last item)
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .align(Alignment.CenterVertically)
                        .padding(top = 16.dp)
                        .background(
                            if (stepStatus.ordinal < status.ordinal) Color(0xFF66BB6A)
                            else Color(0xFFE0E0E0)
                        )
                )
            }
        }
    }
}

