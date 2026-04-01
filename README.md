# JobHunter Android Application (DACS4) 🚀

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=flat&logo=jetpack-compose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM_%2B_Clean-orange?style=flat)

> **Dự án Kỹ nghệ phần mềm (DACS4)**: Chuyển đổi hệ thống Web Tuyển dụng sang nền tảng Ứng dụng Di động Android Native.

## 📖 Tổng quan dự án (Project Overview)

**JobHunter Mobile** là phiên bản ứng dụng di động nguyên bản (Native App) dành cho Ứng viên (Candidates), kết nối trực tiếp với hệ thống Backend Spring Boot RESTful API. Hệ thống tập trung giải quyết bài toán trải nghiệm người dùng di động mượt mà, tốc độ phản hồi nhanh, cùng kiến trúc bảo mật Token khép kín.

## 🌟 Tính năng nổi bật (Key Features)

- **Xác thực An toàn (Secure Authentication)**: Đăng nhập bọc JWT, bảo vệ bằng mã hóa chuẩn AES-256 (`EncryptedSharedPreferences`).
- **Tự động Làm mới Phiên (Silent Token Refreshing)**: Cơ chế Interceptor và Authenticator ở tầng mạng lưới tự động bắt lỗi `401 Unauthorized` và cấp lại Token ngầm định không làm gián đoạn người dùng.
- **Trải nghiệm Mượt mà (Fluid UX)**: Cập nhật giao diện siêu tốc nhờ triết lý `StateFlow` + `Jetpack Compose`. Không xảy ra hiện tượng đơ giật hình (ANR) trong quá trình kết nối mạng.
- **Tối ưu Hóa Hiện thị (UI Optimization)**: Cuộn danh sách Việc làm & Công ty bằng `LazyColumn`, tải và lưu đệm (cache) tự động hình ảnh bằng thư viện `Coil`.

## 🛠️ Công nghệ Lõi (Tech Stack)

Ứng dụng được thiết kế theo tiêu chuẩn công nghiệp hiện đại của Google:

- **Ngôn ngữ**: `Kotlin`
- **Giao diện Người dùng**: `Jetpack Compose` (Modern Declarative UI) / Material Design 3
- **Mô hình Kiến trúc**: `MVVM` (Model - View - ViewModel) + Clean Architecture
- **Tiêm phụ thuộc (DI)**: `Hilt` / Dagger
- **Giao tiếp Mạng lưới (Networking)**: `Retrofit2`, `OkHttp3`, `Gson`
- **Bất đồng bộ & Đa luồng xử lý**: `Kotlin Coroutines`, `Flow/StateFlow`
- **Quản lý Hình ảnh**: `Coil`
- **Bảo mật Nội bộ**: `androidx.security.crypto`

## 📂 Cấu trúc Thư mục Nguồn (Folder Structure)

Kiến trúc mã nguồn được phân chia theo tính năng (Feature-based Modularization):

```text
com.example.dacs4
│
├── core/
│   ├── network/       # Cấu hình đánh chặn gói tin (AuthInterceptor, TokenAuthenticator)
│   └── security/      # Lớp mã hóa ổ cứng và lưu trữ JWT (TokenManager)
│
├── di/
│   └── NetworkModule  # Nhà máy Hilt cấp phát Retrofit, OkHttp Client độc bản
│
├── data/
│   ├── model/         # Các Data Classes (Requests/Responses DTO)
│   ├── remote/        # Định nghĩa các Endpoints (ApiService interface)
│   └── repository/    # Trung gian kết nối dữ liệu, bắt lỗi ngoại lệ cấp cao
│
├── ui/
│   ├── components/    # Các thẻ UI hiển thị dùng chung (JobCard, CompanyCard)
│   ├── navigation/    # Phân luồng điều hướng Màn hình
│   └── screens/       # Các Trang chủ đạo (LoginScreen, HomeScreen) kèm ViewModel
│
├── MainActivity       # Window Container khởi động Compose
└── JobHunterApp       # Lớp gốc gắn thẻ @HiltAndroidApp
```

## 🚀 Hướng dẫn Cài đặt & Chạy (Getting Started)

### 1. Yêu cầu Hệ thống
- Android Studio Iguana (hoặc mới hơn).
- Gradle 8.0+
- Thiết bị ảo (Emulator) hoặc máy thật chạy Android 8.0 (API 26) trở lên.

### 2. Triển khai
1. Clone dự án này về máy:
   ```bash
   git clone [YOUR_GITHUB_REACT_URL_OR_ANDROID_URL_HERE]
   ```
2. Mở thư mục bằng Android Studio.
3. Chờ Gradle đồng bộ hóa các thư viện phụ thuộc (Sync Libraries).
4. Kiểm tra file `app/build.gradle.kts`, chỉnh sửa biến đại diện `BASE_URL` sao cho trỏ khớp đúng vào địa chỉ IP của máy chủ Backend Spring Boot hiện tại (Ví dụ: `http://10.0.2.2:8080/` đối với Emulator).
5. Bấm nút **Run** (mũi tên xanh lá) để cài file APK lên Emulator.

---
*Built with ❤️ for Software Engineering Midterm Defense.*
