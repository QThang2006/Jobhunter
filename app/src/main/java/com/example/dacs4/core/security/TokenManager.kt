package com.example.dacs4.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lớp Quản lý Token dựa trên EncryptedSharedPreferences của Android Crypto.
 * Dữ liệu JWT (access_token) sẽ được mã hoá bằng thuật toán AES-256-GCM.
 * Máy đã root hay hacker mở file XML của App cũng chỉ thấy các chuỗi vô nghĩa.
 */
@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    // Tạo MasterKey chuẩn quân sự để khoá "Két"
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Khởi tạo Két sắt EncryptedSharedPreferences
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "jobhunter_secure_prefs", // Tên file két
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        // Có thể lưu thêm User ID hoặc Role tại đây nếu cần
    }

    // --------------------------------------------------------
    // MŨI TÊN CHÍNH: HÚT / BƠM TOKEN VÀO KÉT
    // --------------------------------------------------------

    // Lưu Token vừa từ API đăng nhập trả về
    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    // Lấy Token ra để gắn vào mọi Request API
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    // Xóa Token khi người dùng Đăng Xuất (Logout) 
    fun clearToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
    }

    // Kiểm tra xem đã Đăng nhập chưa
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
