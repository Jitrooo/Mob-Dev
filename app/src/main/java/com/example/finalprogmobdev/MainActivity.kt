package com.example.finalprogmobdev

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.finalprogmobdev.ui.theme.FinalProgMobDevTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check Firebase connection
        checkFirebaseConnection()

        enableEdgeToEdge()

        // Show UI FIRST
        setContent {
            FinalProgMobDevTheme {
                BookstoreApp()
            }
        }

        // THEN load Firebase data in background (non-blocking)
        lifecycleScope.launch {
            loadFirebaseData()
        }
    }

    private fun checkFirebaseConnection() {
        try {
            // Check Firebase Auth
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "✅ Firebase Auth initialized: ${auth.app.name}")
            Log.d(TAG, "Current user: ${auth.currentUser?.email ?: "No user logged in"}")

            // Check Firestore
            val firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "✅ Firestore initialized: ${firestore.app.name}")

            // Test Firestore connection
            firestore.collection("products").limit(1).get()
                .addOnSuccessListener { documents ->
                    Log.d(TAG, "✅ Firestore connection successful! Found ${documents.size()} products")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "❌ Firestore connection failed: ${e.message}")
                }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase initialization failed: ${e.message}")
        }
    }

    private suspend fun loadFirebaseData() {
        Log.d(TAG, "Loading Firebase data...")

        // Load products from Firebase
        FirebaseManager.loadProducts().onSuccess { firebaseProducts ->
            if (firebaseProducts.isNotEmpty()) {
                Log.d(TAG, "✅ Loaded ${firebaseProducts.size} products from Firebase")

                // Update products with Firebase stock data
                firebaseProducts.forEach { firebaseProduct ->
                    val localProduct = ProductRepository.allProducts.find { it.id == firebaseProduct.id }
                    localProduct?.stock = firebaseProduct.stock
                }
                Log.d(TAG, "✅ Updated local products with Firebase stock data")
            } else {
                Log.w(TAG, "⚠️ No products found in Firebase, initializing...")
                // No products in Firebase, initialize them (ONLY RUN ONCE!)
                FirebaseManager.initializeProducts().onSuccess {
                    Log.d(TAG, "✅ Products initialized in Firebase")
                }.onFailure { e ->
                    Log.e(TAG, "❌ Failed to initialize products: ${e.message}")
                }
            }
        }.onFailure { e ->
            Log.e(TAG, "❌ Failed to load products from Firebase: ${e.message}")
            // Continue with local products
        }
    }
}