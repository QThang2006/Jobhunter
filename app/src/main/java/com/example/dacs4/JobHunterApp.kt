package com.example.dacs4

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Lớp khởi tạo cao nhất của ứng dụng.
 * Annotation @HiltAndroidApp sẽ kích hoạt quá trình tự động sinh mã của Hilt (Dependency Injection),
 * thiết lập Application-level Component để có thể tiêm các phụ thuộc (Dependencies) 
 * (như API Client, Database, SharedPreferences) vào bất cứ đâu trong dự án.
 */
@HiltAndroidApp
class JobHunterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Các logic khởi tạo khác (nếu có) ví dụ định cấu hình cho Thư viện ảnh Coil.
    }
}
