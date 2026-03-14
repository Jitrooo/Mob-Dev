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
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val transactionNumber: String,
    val orderId: String,
    val customerName: String,
    val purchaseDate: String,
    val purchaseTime: String,
    val items: List<OrderItem>,
    val total: Double,
    val paymentMethod: String = "Cash on Pickup"
)

@Composable
fun SellerTransactionsScreen(
    onBackClick: () -> Unit
) {
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var totalRevenue by remember { mutableStateOf(0.0) }
    val scope = rememberCoroutineScope()

    // Load transactions from Firebase
    LaunchedEffect(Unit) {
        scope.launch {
            FirebaseManager.loadAllOrders().onSuccess { orders ->
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                transactions = orders.mapIndexed { index, order ->
                    val date = try {
                        dateFormat.parse(order.date)
                    } catch (e: Exception) {
                        Date()
                    }

                    Transaction(
                        transactionNumber = "TXN-${System.currentTimeMillis() + index}",
                        orderId = order.orderId,
                        customerName = order.customerName,
                        purchaseDate = order.date,
                        purchaseTime = timeFormat.format(date ?: Date()),
                        items = order.items,
                        total = order.total,
                        paymentMethod = "Cash on Pickup"
                    )
                }

                totalRevenue = transactions.sumOf { it.total }
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Transaction History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F1E8))
                .padding(paddingValues)
        ) {
            // Revenue summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF66BB6A)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
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
                        Text(
                            text = "${transactions.size} Transactions",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE31C3D))
                }
            } else if (transactions.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = "No Transactions",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No transactions yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C5C5C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Transaction history will appear here",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            } else {
                // Transaction list
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionCard(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
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
            // Transaction header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = transaction.transactionNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C)
                    )
                    Text(
                        text = transaction.orderId,
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Surface(
                    color = Color(0xFF66BB6A).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Completed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF66BB6A),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Customer info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF5C5C5C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = transaction.customerName,
                    fontSize = 14.sp,
                    color = Color(0xFF5C5C5C)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date and time
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF5C5C5C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = transaction.purchaseDate,
                        fontSize = 13.sp,
                        color = Color(0xFF5C5C5C)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF5C5C5C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = transaction.purchaseTime,
                        fontSize = 13.sp,
                        color = Color(0xFF5C5C5C)
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFE0E0E0)
            )

            // Items
            transaction.items.forEach { item ->
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
                            text = "Qty: ${item.quantity} × Php.%.2f".format(item.price),
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

            // Payment method and total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Payment Method",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        text = transaction.paymentMethod,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5C5C5C)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Text(
                        text = "Php.%.2f".format(transaction.total),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE31C3D)
                    )
                }
            }
        }
    }
}