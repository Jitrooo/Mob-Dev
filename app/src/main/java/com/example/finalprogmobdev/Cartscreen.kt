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

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    val imageRes: Int,
    var quantity: Int,
    val selectedSize: String? = null
)

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Cart") }

    // Use the global shopping cart
    val cartItems = ShoppingCart.items
    val total = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F1E8),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Cart",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F1E8))
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                // Empty cart state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C5C5C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add some items to get started",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE31C3D)
                        )
                    ) {
                        Text("Start Shopping")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Cart items list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            CartItemCard(
                                item = item,
                                onQuantityChange = { newQuantity ->
                                    ShoppingCart.updateQuantity(item.id, newQuantity)
                                },
                                onRemove = {
                                    ShoppingCart.removeItem(item.id)
                                }
                            )
                        }
                    }

                    // Total and checkout section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFCCCCCC))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                            Text(
                                text = "Php.%.2f".format(total),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE31C3D)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CHECKOUT NOW",
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
}

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
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
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Php.%.2f".format(item.price),
                    fontSize = 14.sp,
                    color = Color(0xFF5C5C5C)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = Color(0xFF2C2C2C),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )

                IconButton(
                    onClick = {
                        if (item.quantity > 1) {
                            onQuantityChange(item.quantity - 1)
                        } else {
                            onRemove()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = Color(0xFF2C2C2C),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}