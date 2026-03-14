package com.example.finalprogmobdev

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
fun SellerInventoryScreen(
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var updateTrigger by remember { mutableStateOf(0) }

    // Force full recomposition on update
    key(updateTrigger) {
        SellerInventoryContent(
            selectedCategory = selectedCategory,
            onCategoryChange = { selectedCategory = it },
            onBackClick = onBackClick,
            onStockUpdate = { updateTrigger++ }
        )
    }
}

@Composable
private fun SellerInventoryContent(
    selectedCategory: ProductCategory?,
    onCategoryChange: (ProductCategory?) -> Unit,
    onBackClick: () -> Unit,
    onStockUpdate: () -> Unit
) {
    val products = ProductRepository.allProducts

    val filteredProducts = if (selectedCategory != null) {
        products.filter { it.category == selectedCategory }
    } else {
        products
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
                        text = "Product Inventory",
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
            // Category filter - Scrollable
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategoryChange(null) },
                        label = { Text("All") }
                    )
                }

                items(ProductCategory.entries.toTypedArray()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.name) }
                    )
                }
            }

            // Product list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredProducts,
                    key = { it.id }
                ) { product ->
                    InventoryProductCard(
                        product = product,
                        onStockChange = onStockUpdate
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryProductCard(
    product: Product,
    onStockChange: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentStock by remember(product.id) { mutableIntStateOf(product.stock) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C)
                )
                Text(
                    text = "Php.%.2f".format(product.price),
                    fontSize = 14.sp,
                    color = Color(0xFF5C5C5C)
                )

                // Stock status
                val stockColor = when {
                    currentStock == 0 -> Color(0xFFE31C3D)
                    currentStock < 10 -> Color(0xFFFF9800)
                    else -> Color(0xFF4CAF50)
                }

                Text(
                    text = if (currentStock == 0) "SOLD OUT" else "Stock: $currentStock",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = stockColor
                )
            }

            // Stock controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        val newStock = currentStock + 10
                        product.stock = newStock
                        currentStock = newStock
                        scope.launch {
                            FirebaseManager.updateProductStock(product.id, newStock)
                        }
                        onStockChange()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Stock",
                        tint = Color(0xFF4CAF50)
                    )
                }

                Text(
                    text = "$currentStock",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        if (currentStock >= 10) {
                            val newStock = currentStock - 10
                            product.stock = newStock
                            currentStock = newStock
                            scope.launch {
                                FirebaseManager.updateProductStock(product.id, newStock)
                            }
                            onStockChange()
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Reduce Stock",
                        tint = Color(0xFFE31C3D)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Mark as sold out button
            IconButton(
                onClick = {
                    product.stock = 0
                    currentStock = 0
                    scope.launch {
                        FirebaseManager.updateProductStock(product.id, 0)
                    }
                    onStockChange()
                }
            ) {
                Icon(
                    Icons.Default.Block,
                    contentDescription = "Mark Sold Out",
                    tint = Color(0xFFE31C3D)
                )
            }
        }
    }
}