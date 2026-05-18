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
        private const val KEY_USER_ID      = "user_id"
        private const val KEY_USER_EMAIL   = "user_email"
        private const val KEY_USER_NAME    = "user_name"
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

    // Lưu thông tin user sau khi đăng nhập thành công
    fun saveUserInfo(userId: String, email: String, name: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun getUserId(): String? = sharedPreferences.getString(KEY_USER_ID, null)
    fun getUserEmail(): String? = sharedPreferences.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = sharedPreferences.getString(KEY_USER_NAME, null)

    // Xóa Token và toàn bộ dữ liệu khi người dùng Đăng Xuất
    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply()
    }

    // Kiểm tra xem đã Đăng nhập chưa
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
