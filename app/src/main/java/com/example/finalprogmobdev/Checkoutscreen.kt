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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Global order storage
object OrderDatabase {
    val allOrders = mutableStateListOf<CustomerOrder>()

    private var orderCounter = 1

    fun createOrder(items: List<CartItem>, total: Double): CustomerOrder {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val order = CustomerOrder(
            orderId = "#ORD-2026-%03d".format(orderCounter++),
            customerName = "Customer", // In real app, get from logged-in user
            date = dateFormat.format(Date()),
            items = items.map { cartItem ->
                OrderItem(
                    name = cartItem.name,
                    quantity = cartItem.quantity,
                    price = cartItem.price,
                    imageRes = cartItem.imageRes
                )
            },
            total = total,
            pickupStatus = PickupStatus.PREPARING_ORDER
        )
        allOrders.add(order)
        return order
    }
}

@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    var showConfirmation by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val orderItems = ShoppingCart.items.toList()
    val subtotal = orderItems.sumOf { it.price * it.quantity }
    val shippingFee = 0.0 // Pickup basis, no shipping
    val total = subtotal + shippingFee

    if (showConfirmation) {
        OrderConfirmationScreen(
            onTrackOrder = onPlaceOrder,
            onBackToHome = onPlaceOrder
        )
    } else {
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
                            text = "Checkout",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
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
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Pickup Location",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C2C2C)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "MMCM Campus Bookstore\nDavao City, Philippines",
                                    fontSize = 14.sp,
                                    color = Color(0xFF5C5C5C)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Order Summary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        )
                    }

                    items(orderItems) { item ->
                        CheckoutItemCard(
                            name = item.name,
                            quantity = item.quantity,
                            price = item.price,
                            imageRes = item.imageRes
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal:", color = Color(0xFF5C5C5C))
                                    Text("Php.%.2f".format(subtotal), fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pickup Fee:", color = Color(0xFF5C5C5C))
                                    Text("FREE", fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                                }
                                Divider(modifier = Modifier.padding(vertical = 12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Total:",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C2C2C)
                                    )
                                    Text(
                                        "Php.%.2f".format(total),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE31C3D)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            // Create order object
                            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            val orderId = "#ORD-2026-%03d".format(System.currentTimeMillis() % 1000)

                            val order = CustomerOrder(
                                orderId = orderId,
                                customerName = UserProfile.studentName,
                                date = dateFormat.format(Date()),
                                items = orderItems.map { cartItem ->
                                    OrderItem(
                                        name = cartItem.name,
                                        quantity = cartItem.quantity,
                                        price = cartItem.price,
                                        imageRes = cartItem.imageRes
                                    )
                                },
                                total = total,
                                pickupStatus = PickupStatus.PREPARING_ORDER
                            )

                            // Save order to Firebase
                            val result = FirebaseManager.createOrder(order)

                            result.onSuccess {
                                // Reduce stock for each purchased item
                                orderItems.forEach { cartItem ->
                                    // Find product in repository
                                    val product = ProductRepository.allProducts.find {
                                        it.name == cartItem.name.substringBefore(" (") // Remove size from name
                                    }

                                    if (product != null) {
                                        // Reduce stock locally
                                        product.stock = maxOf(0, product.stock - cartItem.quantity)

                                        // Update stock in Firebase
                                        FirebaseManager.updateProductStock(product.id, product.stock)
                                    }
                                }

                                // Clear cart and show confirmation
                                ShoppingCart.clear()
                                showConfirmation = true
                                isLoading = false
                            }.onFailure { error ->
                                // Handle error - still clear cart but could show error message
                                ShoppingCart.clear()
                                showConfirmation = true
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE31C3D)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "PLACE ORDER",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutItemCard(
    name: String,
    quantity: Int,
    price: Double,
    imageRes: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "Qty: $quantity",
                    fontSize = 14.sp,
                    color = Color(0xFF5C5C5C)
                )
            }

            Text(
                text = "Php.%.2f".format(price * quantity),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    onTrackOrder: () -> Unit,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Order Placed Successfully!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your order is being prepared.\nYou can pick it up at MMCM Bookstore.",
                    fontSize = 16.sp,
                    color = Color(0xFF5C5C5C),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE31C3D)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "BACK TO HOME",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}