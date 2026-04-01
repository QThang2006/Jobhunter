package com.example.dacs4.core.network

import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.remote.ApiService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

/**
 * "Người hùng thầm lặng". Bắt trọn những Request bị văng lỗi 401 Unauthorized do Token hết hạn.
 * Nó mô phỏng lại TỰ ĐỘNG THUẬT TOÁN BẤT ĐỒNG BỘ "async-mutex" trong axios-customize.ts của Web.
 */
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: Provider<ApiService> // Dùng Provider để tránh lỗi vòng lặp phụ thuộc (Circular Dependency)
) : Authenticator {

    // Mutex ngăn chặn tình trạng 10 APIs cùng hết hạn gọi Refresh 10 lần làm tạch Server
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = tokenManager.getAccessToken()

        // Tránh vòng lặp vô tận: Nếu API refresh() cũng trả về 401 -> Token Refresh cũng đã chết -> Hủy
        if (response.request.url.encodedPath.contains("/api/v1/auth/refresh")) {
            return null // Trả về null sẽ đá văng lỗi ra ngoài ViewModel để văng ra màn hình Login
        }

        return runBlocking {
            var requestToTryAgain: Request? = null

            // KHOÁ LẠI: Chỉ cho đúng 1 luồng mạng đầu tiên bị lỗi 401 chui vào đây đi refresh
            mutex.withLock {
                val tokenInKét = tokenManager.getAccessToken()
                
                // Nếu một luồng phía trước ĐÃ refresh thành công và cục TokenInKét đã ĐỔI MỚI so với currentToken
                // Thì luồng đứng xếp hàng phía sau không cần gọi hàm Refresh nữa, xài Token mới luôn!
                if (tokenInKét != null && tokenInKét != currentToken) {
                    requestToTryAgain = response.request.newBuilder()
                        .header("Authorization", "Bearer $tokenInKét")
                        .build()
                } else {
                    // Nếu chưa ai refresh, tiến hành gọi API đổi Token mới
                    try {
                        val apiService = apiServiceProvider.get()
                        
                        // Gọi trực tiếp (Dùng Call.execute() để chạy Đồng bộ - Synchronous trên Thread mạng)
                        val refreshResponse = apiService.refreshTokenSync().execute()
                        
                        if (refreshResponse.isSuccessful) {
                            val newAccessToken = refreshResponse.body()?.data?.access_token
                            if (!newAccessToken.isNullOrEmpty()) {
                                // Cất Token mới siêu hot vào Két
                                tokenManager.saveAccessToken(newAccessToken)

                                // Chắp Token MỚI vào cái Request CŨ kĩ vừa bị văng 401 lúc nãy để đi lại phát nữa
                                requestToTryAgain = response.request.newBuilder()
                                    .header("Authorization", "Bearer $newAccessToken")
                                    .build()
                            }
                        } else {
                            // Backend từ chối Refresh Token (User bị khóa mõm, hoặc RefreshToken HTTP-Only Cookies rớt mạng)
                            tokenManager.clearToken()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        tokenManager.clearToken()
                    }
                }
            }
            // Giải phóng lệnh Khóa Mutex để các luồng khác thoát ra

            // Trả RequestToTryAgain về cho OkHttp. Nếu nó khác khull, OkHttp sẽ tự động GỬI LẠI TRONG IM LẶNG.
            requestToTryAgain
        }
    }
}
