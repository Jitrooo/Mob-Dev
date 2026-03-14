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
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

data class PickupOrder(
    val orderId: String,
    val date: String,
    val items: List<OrderItem>,
    val total: Double,
    val pickupStatus: PickupOrderStatus,
    var isPickedUp: Boolean = false
)

enum class PickupOrderStatus {
    PREPARING_ORDER,
    TRANSPORTING,
    READY_TO_PICKUP
}

@Composable
fun TrackOrdersScreen(
    onBackClick: () -> Unit
) {
    var orders by remember { mutableStateOf<List<PickupOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Load user's orders from Firebase
    LaunchedEffect(Unit) {
        val userId = FirebaseManager.getCurrentUserId()
        if (userId != null) {
            scope.launch {
                FirebaseManager.loadUserOrders(userId).onSuccess { customerOrders ->
                    orders = customerOrders.map { customerOrder ->
                        PickupOrder(
                            orderId = customerOrder.orderId,
                            date = customerOrder.date,
                            items = customerOrder.items,
                            total = customerOrder.total,
                            pickupStatus = when (customerOrder.pickupStatus) {
                                PickupStatus.PREPARING_ORDER -> PickupOrderStatus.PREPARING_ORDER
                                PickupStatus.TRANSPORTING -> PickupOrderStatus.TRANSPORTING
                                PickupStatus.READY_TO_PICKUP -> PickupOrderStatus.READY_TO_PICKUP
                            },
                            isPickedUp = false
                        )
                    }
                    isLoading = false
                }.onFailure {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
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
                        text = "Track Orders",
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
                    Icons.Default.LocalShipping,
                    contentDescription = "No Orders",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No orders to track",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C5C5C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your orders will appear here",
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
                    PickupTrackingCard(
                        order = order,
                        onOrderPickedUp = {
                            // Mark order as picked up
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PickupTrackingCard(
    order: PickupOrder,
    onOrderPickedUp: () -> Unit
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

            // Pickup status timeline
            PickupTimeline(status = order.pickupStatus)

            Spacer(modifier = Modifier.height(20.dp))

            // Current status text
            val statusText = when (order.pickupStatus) {
                PickupOrderStatus.PREPARING_ORDER -> "We're preparing your order"
                PickupOrderStatus.TRANSPORTING -> "Your order is being transported to pickup location"
                PickupOrderStatus.READY_TO_PICKUP -> if (order.isPickedUp) "Order completed" else "Your order is ready for pickup!"
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
                        Icons.Default.Store,
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

            // Order Picked Up button (only show if ready and not picked up)
            if (order.pickupStatus == PickupOrderStatus.READY_TO_PICKUP && !order.isPickedUp) {
                Spacer(modifier = Modifier.height(16.dp))

                var isUpdating by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        isUpdating = true
                        scope.launch {
                            // Mark as picked up in local state
                            order.isPickedUp = true
                            // Could also update in Firebase if needed
                            // For now, just update the UI
                            isUpdating = false
                            onOrderPickedUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66BB6A)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isUpdating
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ORDER PICKED UP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Show completion badge if picked up
            if (order.isPickedUp) {
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
fun PickupTimeline(status: PickupOrderStatus) {
    val steps = listOf(
        "Preparing Order" to PickupOrderStatus.PREPARING_ORDER,
        "Transporting" to PickupOrderStatus.TRANSPORTING,
        "Ready to Pickup" to PickupOrderStatus.READY_TO_PICKUP
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