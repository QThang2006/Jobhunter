package com.example.dacs4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * AndroidEntryPoint bảo Hilt rằng Component này có thể bị tiêm Dependencies vào.
 * Lớp này thay thế cho MainActivity cũ sử dụng ViewBinding.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Gọi AppNavigation - Đầu não điều hướng của ứng dụng
                    AppNavigation(tokenManager = tokenManager)
                }
            }
        }
    }
}
