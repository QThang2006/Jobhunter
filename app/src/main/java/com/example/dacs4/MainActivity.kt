package com.example.dacs4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.navigation.AppNavigation
import com.example.dacs4.ui.theme.AppColors
import com.example.dacs4.ui.theme.JobHunterTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobHunterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.BgPrimary
                ) {
                    AppNavigation(tokenManager = tokenManager)
                }
            }
        }
    }
}
