package com.example.dacs4.data.repository

import com.example.dacs4.data.model.*
import com.example.dacs4.data.remote.ApplicationApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════════
//  APPLICATION REPOSITORY — Phase 2
//  Repository layer: xử lý logic gọi API + error handling
//  Trả về Result<T> để ViewModel xử lý state sạch sẽ
// ═══════════════════════════════════════════════════════════════════

@Singleton
class ApplicationRepository @Inject constructor(
    private val apiService: ApplicationApiService
) {

    // ──────────────────────────────────────────────────────────────
    //  UPLOAD CV FILE
    // ──────────────────────────────────────────────────────────────

    /**
     * Upload file CV lên server
     * Trả về URL đầy đủ của file để đính kèm vào đơn ứng tuyển
     */
    suspend fun uploadCvFile(file: File): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "fileUpload",
                file.name,
                requestBody
            )
            val folderBody = "resume".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadFile(multipartBody, folderBody)
            if (response.isSuccessful && response.body()?.data != null) {
                // Ghép URL đầy đủ từ tên file trả về
                val fileName = response.body()!!.data!!.fileName
                buildFileUrl(fileName)
            } else {
                throw Exception(response.body()?.message ?: "Upload thất bại")
            }
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  CREATE APPLICATION
    // ──────────────────────────────────────────────────────────────

    /**
     * Nộp đơn ứng tuyển mới
     */
    suspend fun createApplication(
        cvUrl: String,
        jobId: String,
        jobName: String,
        companyName: String,
        coverLetter: String
    ): Result<ApplicationResponse> = withContext(Dispatchers.IO) {
        runCatching {
            // MOCK: Simulate API delay (xoá khi có API thật)
            delay(1500)
            mockApplications.first().copy(
                id = "app_${System.currentTimeMillis()}",
                jobName = jobName,
                companyName = companyName,
                status = ApplicationStatus.PENDING
            )

            /* REAL API (uncomment khi có backend):
            val request = CreateApplicationRequest(
                url = cvUrl,
                jobId = jobId,
                jobName = jobName,
                companyName = companyName,
                coverLetter = coverLetter
            )
            val response = apiService.createApplication(request)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()!!.data!!
            } else {
                throw Exception(response.body()?.message ?: "Nộp đơn thất bại")
            }
            */
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  GET MY APPLICATIONS
    // ──────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách đơn ứng tuyển của User hiện tại (có phân trang)
     */
    suspend fun getMyApplications(
        page: Int = 1,
        pageSize: Int = 10
    ): Result<List<ApplicationResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            // MOCK: Simulate network delay (xoá khi có API thật)
            delay(800)
            mockApplications

            /* REAL API:
            val response = apiService.getMyApplications(page, pageSize)
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()!!.data!!.result
            } else {
                throw Exception(response.body()?.message ?: "Lấy danh sách thất bại")
            }
            */
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  GET APPLICATION DETAIL
    // ──────────────────────────────────────────────────────────────

    /**
     * Lấy chi tiết một đơn ứng tuyển
     */
    suspend fun getApplicationDetail(id: String): Result<ApplicationResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                delay(600)
                mockApplications.find { it.id == id }
                    ?: throw Exception("Không tìm thấy đơn ứng tuyển")

                /* REAL API:
                val response = apiService.getApplicationDetail(id)
                if (response.isSuccessful && response.body()?.data != null) {
                    response.body()!!.data!!
                } else {
                    throw Exception(response.body()?.message ?: "Lấy chi tiết thất bại")
                }
                */
            }
        }

    // ──────────────────────────────────────────────────────────────
    //  DELETE APPLICATION (Rút đơn)
    // ──────────────────────────────────────────────────────────────

    suspend fun deleteApplication(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            delay(800)
            Unit // Mock success

            /* REAL API:
            val response = apiService.deleteApplication(id)
            if (!response.isSuccessful) {
                throw Exception(response.body()?.message ?: "Rút đơn thất bại")
            }
            */
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  GET SAVED CVS (User's CV library)
    // ──────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách CV đã lưu của User
     * (Thực tế lấy từ GET /api/v1/users/profile hoặc GET /api/v1/resumes/by-user)
     */
    suspend fun getSavedCvs(): Result<List<SavedCv>> = withContext(Dispatchers.IO) {
        runCatching {
            delay(500)
            mockSavedCvs
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  HELPERS
    // ──────────────────────────────────────────────────────────────

    /**
     * Ghép URL đầy đủ từ tên file trả về của API
     * Ví dụ: "cv_abc123.pdf" → "https://[base-url]/images/resume/cv_abc123.pdf"
     */
    private fun buildFileUrl(fileName: String): String {
        val baseUrl = "https://jobhunter-api.example.com"
        return "$baseUrl/images/resume/$fileName"
    }
}
