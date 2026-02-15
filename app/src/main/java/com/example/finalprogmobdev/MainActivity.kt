package com.example.finalprogmobdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.finalprogmobdev.ui.theme.FinalProgMobDevTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalProgMobDevTheme {
                BookstoreApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroductionScreenPreview() {
    FinalProgMobDevTheme {
        IntroductionScreen(
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}