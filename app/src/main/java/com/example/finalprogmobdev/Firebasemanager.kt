package com.example.finalprogmobdev

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Firebase Manager - Handles all Firebase operations
 */
object FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Collections
    private val usersCollection = firestore.collection("users")
    private val productsCollection = firestore.collection("products")
    private val ordersCollection = firestore.collection("orders")

    /**
     * USER AUTHENTICATION
     */

    // Register new user
    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<String> {
        return try {
            // Create auth user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            // Save user data to Firestore
            val userData = hashMapOf(
                "userId" to userId,
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            )

            usersCollection.document(userId).set(userData).await()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login user
    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get current user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Logout user
    fun logoutUser() {
        auth.signOut()
    }

    // Get user data
    suspend fun getUserData(userId: String): Result<Map<String, Any>> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update user name
    suspend fun updateUserName(userId: String, firstName: String, lastName: String): Result<Unit> {
        return try {
            val updates = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName
            )
            usersCollection.document(userId).set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * PRODUCTS
     */

    // Initialize products in Firebase (call this once)
    suspend fun initializeProducts(): Result<Unit> {
        return try {
            ProductRepository.allProducts.forEach { product ->
                val productData = hashMapOf(
                    "id" to product.id,
                    "name" to product.name,
                    "price" to product.price,
                    "category" to product.category.name,
                    "stock" to product.stock,
                    "sizes" to product.sizes
                )
                productsCollection.document(product.id.toString()).set(productData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Load products from Firebase
    suspend fun loadProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    Product(
                        id = (data["id"] as Long).toInt(),
                        name = data["name"] as String,
                        price = (data["price"] as? Double) ?: (data["price"] as Long).toDouble(),
                        category = ProductCategory.valueOf(data["category"] as String),
                        imageRes = R.drawable.mapua_logo, // Using placeholder
                        sizes = (data["sizes"] as? List<String>) ?: emptyList(),
                        stock = (data["stock"] as Long).toInt()
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update product stock
    suspend fun updateProductStock(productId: Int, newStock: Int): Result<Unit> {
        return try {
            val updates = hashMapOf(
                "stock" to newStock
            )
            productsCollection.document(productId.toString())
                .set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ORDERS
     */

    // Create new order
    suspend fun createOrder(order: CustomerOrder): Result<String> {
        return try {
            val orderData = hashMapOf(
                "orderId" to order.orderId,
                "customerName" to order.customerName,
                "userId" to (getCurrentUserId() ?: "unknown"),
                "date" to order.date,
                "items" to order.items.map { item ->
                    hashMapOf(
                        "name" to item.name,
                        "quantity" to item.quantity,
                        "price" to item.price
                    )
                },
                "total" to order.total,
                "pickupStatus" to order.pickupStatus.name,
                "createdAt" to System.currentTimeMillis()
            )

            ordersCollection.document(order.orderId).set(orderData).await()
            Result.success(order.orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Load all orders
    suspend fun loadAllOrders(): Result<List<CustomerOrder>> {
        return try {
            val snapshot = ordersCollection.get().await()
            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    CustomerOrder(
                        orderId = data["orderId"] as String,
                        customerName = data["customerName"] as String,
                        date = data["date"] as String,
                        items = (data["items"] as List<Map<String, Any>>).map { itemMap ->
                            OrderItem(
                                name = itemMap["name"] as String,
                                quantity = (itemMap["quantity"] as Long).toInt(),
                                price = (itemMap["price"] as? Double) ?: (itemMap["price"] as Long).toDouble(),
                                imageRes = R.drawable.mapua_logo
                            )
                        },
                        total = (data["total"] as? Double) ?: (data["total"] as Long).toDouble(),
                        pickupStatus = PickupStatus.valueOf(data["pickupStatus"] as String)
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Load user's orders only
    suspend fun loadUserOrders(userId: String): Result<List<CustomerOrder>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    CustomerOrder(
                        orderId = data["orderId"] as String,
                        customerName = data["customerName"] as String,
                        date = data["date"] as String,
                        items = (data["items"] as List<Map<String, Any>>).map { itemMap ->
                            OrderItem(
                                name = itemMap["name"] as String,
                                quantity = (itemMap["quantity"] as Long).toInt(),
                                price = (itemMap["price"] as? Double) ?: (itemMap["price"] as Long).toDouble(),
                                imageRes = R.drawable.mapua_logo
                            )
                        },
                        total = (data["total"] as? Double) ?: (data["total"] as Long).toDouble(),
                        pickupStatus = PickupStatus.valueOf(data["pickupStatus"] as String)
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update order status
    suspend fun updateOrderStatus(orderId: String, newStatus: PickupStatus): Result<Unit> {
        return try {
            val updates = hashMapOf(
                "pickupStatus" to newStatus.name
            )
            ordersCollection.document(orderId)
                .set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}