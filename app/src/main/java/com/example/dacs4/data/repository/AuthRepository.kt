package com.example.dacs4.data.repository

import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.model.request.LoginRequest
import com.example.dacs4.data.model.response.AuthResponse
import com.example.dacs4.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TẦNG KHO CHỨA (REPOSITORY LAYER) - LUỒNG XÁC THỰC
 * 
 * Nơi này chịu trách nhiệm:
 * 1. Gọi API từ ApiService (Đẩy xuống luồng ngầm IO để không đơ màn hình).
 * 2. Đặt Try/Catch bao bọc mọi sự cố (Ví dụ rớt Wifi đang gọi API).
 * 3. Chuyển đổi dữ liệu thô (JSON) thành Kết quả thành công/thất bại (Result<T>) sạch sẽ 
 *    trước khi ném lên màn hình hiển thị.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Hàm Đăng Nhập An Toàn
     */
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        // withContext(Dispatchers.IO) đẩy lệnh chạy ngầm rẽ nhánh ra khỏi Thread giao diện chính (Main Thread)
        // -> App sẽ cuộn mượt mà ngay cả khi API tải chậm rì!
        return withContext(Dispatchers.IO) {
            try {
                // Bước 1: Gọi hàm POST lên API do Retrofit xây dựng sẵn
                val response = apiService.login(request)
                
                // Bước 2: Kiểm tra nếu Server trả lời thành công (HTTP Code 200..299)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    
                    // Kiểm tra statusCode logic nội bộ của Spring Boot
                    if (body.statusCode in 200..299 && body.data != null) {
                        // Vô mánh! Lưu Token vừa lấy về vào Két sắt 256-bit AES
                        tokenManager.saveAccessToken(body.data.access_token)
                        
                        // Đóng gói data thành công ném ra ngoài
                        Result.success(body.data)
                    } else {
                        // Tài khoản sai mk, email không tồn tại... (Lỗi Validation Spring)
                        Result.failure(Exception(body.message ?: "Tài khoản hoặc mật khẩu không chính xác"))
                    }
                } else {
                    // Lỗi phần cứng máy chủ (Lỗi 403, 404, 500 Internall Error)
                    Result.failure(Exception("Lỗi máy chủ không xác định (Mã lỗi: ${response.code()})"))
                }
            } catch (e: Exception) {
                // Lỗi Mạng Di Động / Cáp Quang biển đứt / Timeout
                Result.failure(Exception("Mất kết nối mạng! Vui lòng kiểm tra Wifi/3G và thử lại."))
            }
        }
    }

    /**
     * Hàm Đăng Xuất & Chùi dọn dữ liệu
     */
    suspend fun logout(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Bước 1: Gửi lệnh rớt API thông báo với Server huỷ Token
                val response = apiService.logout()
                
                // Bước 2: Phải xoá Token trên máy dù server rớt hay thành công
                tokenManager.clearToken()
                
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    // Dù Backend lỗi (VD: Bị khóa tk, xóa tk), User bấm "Đăng Xuất" 
                    // mình vẫn cho thoát sạch App chặn bug "Không Thể Thoát".
                    Result.success(true) 
                }
            } catch (e: Exception) {
                tokenManager.clearToken()
                // Lỗi mạng vẫn cho out ra login sạch sẽ!
                Result.success(true)
            }
        }
    }
}
