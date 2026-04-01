package com.example.dacs4.di

import com.example.dacs4.BuildConfig
import com.example.dacs4.core.network.AuthInterceptor
import com.example.dacs4.core.network.TokenAuthenticator
import com.example.dacs4.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Nhà Máy "Sản Xuất" Đối Tượng bằng Hilt (Dependency Injection).
 * Nơi này sẽ tự động tạo cấu hình Retrofit và OkHttp siêu tối ưu cho toàn bộ ứng dụng.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        // Ánh xạ mọi Network Log vào khung xem Logcat của Android Studio (Bắt lỗi siêu nhanh)
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY // Hiện full request/response json
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Bơm Authorization Header
            .authenticator(tokenAuthenticator) // Cảnh sát bắt lỗi 401
            .addInterceptor(loggingInterceptor) // Theo dõi log
            // CookieJar tự động (Nếu Spring Boot dùng RefreshToken HTTP-Only Cookies)
            // Thêm Timeout an toàn:
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL) // Lấy URL khai báo trong app/build.gradle.kts
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Tự động Map JSON ra đối tượng
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
