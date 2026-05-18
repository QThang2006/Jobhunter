package com.example.dacs4.data.repository

import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.model.request.ChangePasswordRequest
import com.example.dacs4.data.model.request.UpdateUserRequest
import com.example.dacs4.data.model.response.UserDetailResponse
import com.example.dacs4.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun getCurrentUser(): Result<UserDetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = tokenManager.getUserId()?.toLongOrNull()
                    ?: return@withContext Result.failure(Exception("Vui lòng đăng nhập lại"))
                val response = apiService.getUserById(userId)
                if (response.isSuccessful && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception("Không thể tải thông tin người dùng"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    suspend fun updateUser(request: UpdateUserRequest): Result<UserDetailResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateUser(request)
                if (response.isSuccessful && response.body()?.data != null) {
                    // Cập nhật tên mới vào local storage
                    tokenManager.saveUserInfo(
                        userId = request.id.toString(),
                        email  = request.email,
                        name   = request.name
                    )
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Cập nhật thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val email = tokenManager.getUserEmail()
                    ?: return@withContext Result.failure(Exception("Vui lòng đăng nhập lại"))
                val response = apiService.changePassword(
                    ChangePasswordRequest(email = email, oldpass = oldPass, newpass = newPass)
                )
                if (response.isSuccessful) {
                    Result.success(response.body()?.data ?: "Đổi mật khẩu thành công")
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Đổi mật khẩu thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}
