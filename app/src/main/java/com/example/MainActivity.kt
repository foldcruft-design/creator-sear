package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.repository.AuthRepository
import com.example.data.repository.VideoProjectRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.TokEnhanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository(this)
        val projectRepository = VideoProjectRepository(this)

        setContent {
            TokEnhanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkObsidian
                ) {
                    HomeScreen(
                        authRepository = authRepository,
                        projectRepository = projectRepository
                    )
                }
            }
        }
    }
}
