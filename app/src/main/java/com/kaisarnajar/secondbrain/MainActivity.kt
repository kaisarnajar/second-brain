package com.kaisarnajar.secondbrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kaisarnajar.secondbrain.ui.screens.HomeScreen
import com.kaisarnajar.secondbrain.ui.theme.SecondBrainTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecondBrainTheme {
                HomeScreen()
            }
        }
    }
}
