package com.example.finalprogmobdev

import androidx.compose.runtime.mutableStateListOf

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: ProductCategory,
    val imageRes: Int,
    val sizes: List<String> = emptyList(),
    var stock: Int = 100
)

enum class ProductCategory {
    BOOKLETS,
    BALLPENS,
    PAPER,
    WEARABLES,
    MERCH,
    OTHERS
}

object ProductRepository {
    val allProducts = mutableStateListOf(
        // BOOKLETS
        Product(1, "Long Test Booklet", 10.0, ProductCategory.BOOKLETS, R.drawable.booklet, stock = 100),
        Product(2, "Short Test Booklet", 10.0, ProductCategory.BOOKLETS, R.drawable.booklet, stock = 100),
        Product(3, "Printed Test Booklet", 25.0, ProductCategory.BOOKLETS, R.drawable.booklet, stock = 100),

        // BALLPENS
        Product(4, "Ballpen (Black)", 12.0, ProductCategory.BALLPENS, R.drawable.ballpen, stock = 100),
        Product(5, "Ballpen (Blue)", 12.0, ProductCategory.BALLPENS, R.drawable.ballpen, stock = 100),
        Product(6, "Ballpen (Red)", 12.0, ProductCategory.BALLPENS, R.drawable.ballpen, stock = 100),
        Product(7, "Pencil", 8.0, ProductCategory.BALLPENS, R.drawable.pencil, stock = 100),
        Product(8, "Correction Tape", 25.0, ProductCategory.BALLPENS, R.drawable.correction_tape, stock = 100),
        Product(9, "Eraser", 10.0, ProductCategory.BALLPENS, R.drawable.eraser, stock = 100),
        Product(10, "12inch Ruler", 15.0, ProductCategory.BALLPENS, R.drawable.ruler, stock = 100),
        Product(11, "Protractor", 10.0, ProductCategory.BALLPENS, R.drawable.protractor, stock = 100),
        Product(12, "Compass", 35.0, ProductCategory.BALLPENS, R.drawable.compass, stock = 100),

        // PAPER
        Product(13, "1 pad Yellow Paper", 50.0, ProductCategory.PAPER, R.drawable.yellow_paper, stock = 100),
        Product(14, "1 pad One Whole Intermediate Paper", 50.0, ProductCategory.PAPER, R.drawable.intermediate_paper, stock = 100),
        Product(15, "12pcs Long Bond Paper", 60.0, ProductCategory.PAPER, R.drawable.bond_paper, stock = 100),
        Product(16, "12pcs A4 Paper", 60.0, ProductCategory.PAPER, R.drawable.a4_paper, stock = 100),
        Product(17, "12pcs Short Bond Paper", 60.0, ProductCategory.PAPER, R.drawable.bond_paper, stock = 100),
        Product(18, "1pc A3 Paper", 10.0, ProductCategory.PAPER, R.drawable.a3_paper, stock = 100),

        // WEARABLES
        Product(19, "MMCM ID Sling", 100.0, ProductCategory.WEARABLES, R.drawable.id_sling, stock = 100),
        Product(20, "SHS Uniform Polo-Shirt", 360.0, ProductCategory.WEARABLES, R.drawable.uniform_polo, sizes = listOf("S", "M", "L", "XL"), stock = 100),
        Product(21, "MMCM Jogging Pants", 500.0, ProductCategory.WEARABLES, R.drawable.jogging_pants, sizes = listOf("S", "M", "L", "XL"), stock = 100),
        Product(22, "MMCM PE Uniform", 350.0, ProductCategory.WEARABLES, R.drawable.pe_uniform, sizes = listOf("S", "M", "L", "XL"), stock = 100),

        // MERCH
        Product(23, "MMCM Blue Hoodie", 1500.0, ProductCategory.MERCH, R.drawable.blue_hoodie, sizes = listOf("S", "M", "L", "XL"), stock = 100),
        Product(24, "MMCM Red Hoodie", 1500.0, ProductCategory.MERCH, R.drawable.red_hoodie, sizes = listOf("S", "M", "L", "XL"), stock = 100),
        Product(25, "MMCM White Hoodie", 1500.0, ProductCategory.MERCH, R.drawable.white_hoodie, sizes = listOf("S", "M", "L", "XL"), stock = 100),
        Product(26, "MMCM Black Jacket", 1300.0, ProductCategory.MERCH, R.drawable.black_jacket, sizes = listOf("S", "M", "L", "XL"), stock = 100),
        Product(27, "MMCM Mug", 150.0, ProductCategory.MERCH, R.drawable.mug, stock = 100),
        Product(28, "MMCM Sticker Pack", 100.0, ProductCategory.MERCH, R.drawable.sticker_pack, stock = 100)
    )
}