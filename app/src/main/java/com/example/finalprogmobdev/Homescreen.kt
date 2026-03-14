package com.example.finalprogmobdev

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Global shopping cart
object ShoppingCart {
    val items = mutableStateListOf<CartItem>()

    fun addItem(product: Product, selectedSize: String? = null) {
        val existingItem = items.find {
            it.name == product.name &&
                    (selectedSize == null || it.selectedSize == selectedSize)
        }

        if (existingItem != null) {
            val index = items.indexOf(existingItem)
            items[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            items.add(
                CartItem(
                    id = items.size + 1,
                    name = product.name + if (selectedSize != null) " ($selectedSize)" else "",
                    price = product.price,
                    imageRes = product.imageRes,
                    quantity = 1,
                    selectedSize = selectedSize
                )
            )
        }
    }

    fun removeItem(itemId: Int) {
        items.removeAll { it.id == itemId }
    }

    fun updateQuantity(itemId: Int, newQuantity: Int) {
        val item = items.find { it.id == itemId }
        if (item != null) {
            val index = items.indexOf(item)
            if (newQuantity > 0) {
                items[index] = item.copy(quantity = newQuantity)
            } else {
                items.removeAt(index)
            }
        }
    }

    fun clear() {
        items.clear()
    }
}

@Composable
fun HomeScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Home") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddedToast by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showSizeDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var updateTrigger by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Reload products from Firebase when screen appears
    LaunchedEffect(Unit) {
        scope.launch {
            FirebaseManager.loadProducts().onSuccess { firebaseProducts ->
                firebaseProducts.forEach { firebaseProduct ->
                    val localProduct = ProductRepository.allProducts.find { it.id == firebaseProduct.id }
                    localProduct?.stock = firebaseProduct.stock
                }
                updateTrigger++
            }
        }
    }

    // Get all products from repository
    val allProducts = remember(updateTrigger) { ProductRepository.allProducts.toList() }

    // Filter products based on search and category
    val filteredProducts = allProducts.filter { product ->
        val matchesSearch = if (searchQuery.isNotEmpty()) {
            product.name.contains(searchQuery, ignoreCase = true) ||
                    product.category.name.contains(searchQuery, ignoreCase = true)
        } else {
            true
        }

        val matchesCategory = if (selectedCategory != null) {
            product.category == selectedCategory
        } else {
            true
        }

        matchesSearch && matchesCategory
    }

    // Show size selection dialog
    if (showSizeDialog && selectedProduct != null) {
        SizeSelectionDialog(
            product = selectedProduct!!,
            onDismiss = { showSizeDialog = false },
            onSizeSelected = { size ->
                ShoppingCart.addItem(selectedProduct!!, size)
                showSizeDialog = false
                showAddedToast = true
            }
        )
    }

    // Toast message
    if (showAddedToast) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showAddedToast = false
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
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Now", color = Color.Gray) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFFE31C3D)
                        ),
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        shape = RoundedCornerShape(25.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    BadgedBox(
                        badge = {
                            if (ShoppingCart.items.isNotEmpty()) {
                                Badge {
                                    Text(ShoppingCart.items.size.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToCart) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color(0xFFE31C3D),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
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
                    onClick = { selectedTab = "Home" },
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
                    onClick = {
                        selectedTab = "Search"
                        onNavigateToProducts()
                    },
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
                    onClick = {
                        selectedTab = "Cart"
                        onNavigateToCart()
                    },
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
                    onClick = {
                        selectedTab = "Profile"
                        onNavigateToProfile()
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE31C3D),
                        selectedTextColor = Color(0xFFE31C3D),
                        indicatorColor = Color(0xFFFFE5E5)
                    )
                )
            }
        },
        snackbarHost = {
            if (showAddedToast) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Text("Added to cart!")
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
            // Category tabs with "All" option
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            selectedContainerColor = Color(0xFFE31C3D),
                            labelColor = Color(0xFF2C2C2C),
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(ProductCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.name.replace("_", " ")) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            selectedContainerColor = Color(0xFFE31C3D),
                            labelColor = Color(0xFF2C2C2C),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Show filtered products
            if (filteredProducts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = "No results",
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No products found",
                        fontSize = 18.sp,
                        color = Color(0xFF5C5C5C)
                    )
                }
            } else {
                if (searchQuery.isNotEmpty() || selectedCategory != null) {
                    SectionTitle("${filteredProducts.size} Product(s)")
                } else {
                    SectionTitle("All Products")
                }

                filteredProducts.forEach { product ->
                    ProductCard(
                        imageRes = product.imageRes,
                        title = product.name,
                        price = "Php.%.2f".format(product.price),
                        stock = product.stock,
                        onAddToCart = {
                            if (product.stock > 0) {
                                if (product.sizes.isNotEmpty()) {
                                    selectedProduct = product
                                    showSizeDialog = true
                                } else {
                                    ShoppingCart.addItem(product)
                                    showAddedToast = true
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SizeSelectionDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSizeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Size") },
        text = {
            Column {
                Text("Choose size for ${product.name}")
                Spacer(modifier = Modifier.height(16.dp))
                product.sizes.forEach { size ->
                    Button(
                        onClick = { onSizeSelected(size) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE31C3D)
                        )
                    ) {
                        Text(size)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2C2C2C),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ProductCard(
    imageRes: Int,
    title: String,
    price: String,
    stock: Int,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = price,
                    fontSize = 14.sp,
                    color = Color(0xFF5C5C5C)
                )
                if (stock == 0) {
                    Text(
                        text = "OUT OF STOCK",
                        fontSize = 12.sp,
                        color = Color(0xFFE31C3D),
                        fontWeight = FontWeight.Bold
                    )
                } else if (stock < 10) {
                    Text(
                        text = "Only $stock left!",
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onAddToCart,
                enabled = stock > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (stock > 0) "Add To Cart" else "Sold Out",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}