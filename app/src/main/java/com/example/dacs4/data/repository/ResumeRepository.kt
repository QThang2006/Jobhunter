package com.example.dacs4.data.repository

import android.content.Context
import android.provider.OpenableColumns
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.model.request.CreateResumeRequest
import com.example.dacs4.data.model.request.IdWrapper
import com.example.dacs4.data.model.response.PaginationData
import com.example.dacs4.data.model.response.ResumeResponse
import com.example.dacs4.data.remote.ApiService
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResumeRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Upload file CV lên server, trả về tên file được lưu
     */
    suspend fun uploadCv(uri: Uri, context: Context): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@withContext Result.failure(Exception("Không thể đọc file"))
                val fileName    = getFileNameFromUri(uri, context)
                val mimeType    = context.contentResolver.getType(uri) ?: "application/octet-stream"
                val requestBody = inputStream.readBytes().toRequestBody(mimeType.toMediaType())
                val filePart    = MultipartBody.Part.createFormData("file", fileName, requestBody)
                val folderPart  = "resume".toRequestBody("text/plain".toMediaType())

                val response = apiService.uploadFile(filePart, folderPart)
                if (response.isSuccessful && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!.fileName)
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Upload thất bại"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi khi upload file: ${e.message}"))
            }
        }
    }

    /**
     * Ứng tuyển việc làm — gọi sau khi upload CV thành công
     */
    suspend fun applyJob(jobId: Long, cvFileName: String): Result<ResumeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userId  = tokenManager.getUserId()?.toLongOrNull()
                    ?: return@withContext Result.failure(Exception("Vui lòng đăng nhập lại"))
                val email   = tokenManager.getUserEmail()
                    ?: return@withContext Result.failure(Exception("Vui lòng đăng nhập lại"))

                val request = CreateResumeRequest(
                    email = email,
                    url   = cvFileName,
                    user  = IdWrapper(userId),
                    job   = IdWrapper(jobId)
                )
                val response = apiService.createResume(request)
                if (response.isSuccessful && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!)
                } else {
                    val msg = response.body()?.message ?: "Ứng tuyển thất bại"
                    val userMsg = when {
                        msg.contains("đã nộp cv", ignoreCase = true) ->
                            "Bạn đã ứng tuyển đợt này rồi! Hãy chờ kết quả."
                        else -> msg
                    }
                    Result.failure(Exception(userMsg))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    /**
     * Lấy lịch sử ứng tuyển của user hiện tại
     */
    suspend fun getMyResumes(page: Int = 1): Result<PaginationData<ResumeResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMyResumes(page = page, pageSize = 20)
                if (response.isSuccessful && response.body()?.data != null) {
                    Result.success(response.body()!!.data!!)
                } else {
                    Result.failure(Exception("Không thể tải lịch sử ứng tuyển"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri, context: Context): String {
        var name = "cv_${System.currentTimeMillis()}.pdf"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && idx >= 0) name = cursor.getString(idx)
        }
        return name
    }
}
