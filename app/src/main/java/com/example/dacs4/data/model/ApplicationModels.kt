package com.example.dacs4.data.model

// ═══════════════════════════════════════════════════════════════════
//  APPLICATION MODELS — Phase 2
//  Request & Response DTOs cho luồng ứng tuyển
// ═══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────────────────────────
//  REQUEST DTOs
// ──────────────────────────────────────────────────────────────────

/**
 * Body gửi lên khi nộp đơn ứng tuyển
 * POST /api/v1/resumes
 */
data class CreateApplicationRequest(
    val url: String,              // URL của CV đã upload lên server
    val companyName: String,      // Tên công ty
    val jobName: String,          // Tên vị trí
    val jobId: String,            // ID của Job
    val coverLetter: String = ""  // Thư giới thiệu (tuỳ chọn)
)

/**
 * Body upload file CV lên server
 * POST /api/v1/files
 */
data class UploadFileRequest(
    val folder: String = "resume", // Thư mục lưu trên server (mặc định: resume)
    val fileType: String = "pdf"   // Loại file
)

// ──────────────────────────────────────────────────────────────────
//  RESPONSE DTOs
// ──────────────────────────────────────────────────────────────────

/**
 * Thông tin một đơn ứng tuyển
 * Tương đương interface IResume trên backend
 */
data class ApplicationResponse(
    val id: String,
    val url: String,                    // URL file CV
    val companyName: String,
    val jobName: String,
    val status: ApplicationStatus,      // Trạng thái đơn
    val coverLetter: String?,
    val createdAt: String,
    val updatedAt: String,
    val job: JobSummary?,               // Thông tin job (nullable: có thể job đã bị xoá)
    val company: CompanySummary?,
    val user: UserSummary?
)

/**
 * Trạng thái đơn ứng tuyển – mapping với backend enum
 */
enum class ApplicationStatus(val displayName: String, val colorHex: String) {
    PENDING("Đang chờ duyệt", "#F59E0B"),
    REVIEWING("Đang xem xét", "#6366F1"),
    APPROVED("Đã chấp nhận", "#10B981"),
    REJECTED("Không phù hợp", "#EF4444");

    companion object {
        fun fromString(value: String): ApplicationStatus = when (value.uppercase()) {
            "PENDING"   -> PENDING
            "REVIEWING" -> REVIEWING
            "APPROVED"  -> APPROVED
            "REJECTED"  -> REJECTED
            else        -> PENDING
        }
    }
}

/**
 * Tóm tắt thông tin Job (dùng trong ApplicationResponse)
 */
data class JobSummary(
    val id: String,
    val name: String,
    val salary: Long?,
    val location: String?
)

/**
 * Tóm tắt thông tin Company (dùng trong ApplicationResponse)
 */
data class CompanySummary(
    val id: String,
    val name: String,
    val logo: String?
)

/**
 * Tóm tắt thông tin User (dùng trong ApplicationResponse)
 */
data class UserSummary(
    val id: String,
    val name: String,
    val email: String
)

/**
 * Response sau khi upload file thành công
 * POST /api/v1/files → { fileName }
 */
data class UploadFileResponse(
    val fileName: String   // Tên file trên server – dùng để ghép thành URL đầy đủ
)

/**
 * CV đã lưu trong profile của user (danh sách để chọn khi ứng tuyển)
 * Đây là dữ liệu local/mock – thực tế lấy từ profile API
 */
data class SavedCv(
    val id: String,
    val name: String,           // Tên hiển thị (vd: "CV_NguyenVanA_2024.pdf")
    val url: String,            // URL file
    val uploadedAt: String,     // Ngày upload
    val isDefault: Boolean = false
)

/**
 * Wrapper cho danh sách applications có phân trang
 */
data class ApplicationListResponse(
    val meta: PaginationMeta,
    val result: List<ApplicationResponse>
)

data class PaginationMeta(
    val current: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Long
)

// ──────────────────────────────────────────────────────────────────
//  UI STATE MODELS
// ──────────────────────────────────────────────────────────────────

/**
 * Các bước trong multi-step application form
 */
enum class ApplyStep {
    SELECT_CV,      // Bước 1: Chọn CV
    COVER_LETTER,   // Bước 2: Viết thư giới thiệu
    CONFIRM         // Bước 3: Xác nhận & gửi
}

/**
 * Nguồn CV được chọn
 */
sealed class CvSource {
    object None : CvSource()
    data class FromDevice(val uri: String, val fileName: String, val fileSize: Long) : CvSource()
    data class FromSaved(val cv: SavedCv) : CvSource()
}

// ──────────────────────────────────────────────────────────────────
//  MOCK DATA (dùng cho Phase 2 khi chưa có API thật)
// ──────────────────────────────────────────────────────────────────

val mockSavedCvs = listOf(
    SavedCv(
        id = "cv1",
        name = "CV_Developer_2025.pdf",
        url = "https://files.jobhunter.vn/resumes/cv1.pdf",
        uploadedAt = "15/01/2025",
        isDefault = true
    ),
    SavedCv(
        id = "cv2",
        name = "CV_FullStack_Tiki_2024.pdf",
        url = "https://files.jobhunter.vn/resumes/cv2.pdf",
        uploadedAt = "03/11/2024"
    ),
    SavedCv(
        id = "cv3",
        name = "CV_Senior_Android_VNG.pdf",
        url = "https://files.jobhunter.vn/resumes/cv3.pdf",
        uploadedAt = "28/09/2024"
    )
)

val mockApplications = listOf(
    ApplicationResponse(
        id = "app1",
        url = "https://files.jobhunter.vn/resumes/cv1.pdf",
        companyName = "VNG Corporation",
        jobName = "Senior Android Developer",
        status = ApplicationStatus.REVIEWING,
        coverLetter = "Tôi có 4 năm kinh nghiệm Kotlin và rất muốn góp sức cho VNG...",
        createdAt = "2025-01-15T09:30:00",
        updatedAt = "2025-01-16T14:00:00",
        job = JobSummary("1", "Senior Android Developer", 4000, "TP. Hồ Chí Minh"),
        company = CompanySummary("c1", "VNG Corporation", null),
        user = null
    ),
    ApplicationResponse(
        id = "app2",
        url = "https://files.jobhunter.vn/resumes/cv2.pdf",
        companyName = "Tiki",
        jobName = "Full-Stack Engineer",
        status = ApplicationStatus.APPROVED,
        coverLetter = null,
        createdAt = "2025-01-10T10:00:00",
        updatedAt = "2025-01-13T11:30:00",
        job = JobSummary("2", "Full-Stack Engineer", 3200, "Hà Nội"),
        company = CompanySummary("c2", "Tiki", null),
        user = null
    ),
    ApplicationResponse(
        id = "app3",
        url = "https://files.jobhunter.vn/resumes/cv1.pdf",
        companyName = "Shopee Vietnam",
        jobName = "DevOps Engineer",
        status = ApplicationStatus.PENDING,
        coverLetter = null,
        createdAt = "2025-01-18T08:00:00",
        updatedAt = "2025-01-18T08:00:00",
        job = JobSummary("5", "DevOps Engineer", 3800, "TP. Hồ Chí Minh"),
        company = CompanySummary("c5", "Shopee Vietnam", null),
        user = null
    ),
    ApplicationResponse(
        id = "app4",
        url = "https://files.jobhunter.vn/resumes/cv3.pdf",
        companyName = "Momo",
        jobName = "iOS Developer",
        status = ApplicationStatus.REJECTED,
        coverLetter = "Tôi muốn được thử sức ở mảng iOS tại Momo...",
        createdAt = "2024-12-20T14:00:00",
        updatedAt = "2024-12-28T09:00:00",
        job = JobSummary("3", "iOS Developer", 3500, "Remote"),
        company = CompanySummary("c3", "Momo", null),
        user = null
    ),
    ApplicationResponse(
        id = "app5",
        url = "https://files.jobhunter.vn/resumes/cv1.pdf",
        companyName = "Zalo AI",
        jobName = "Machine Learning Engineer",
        status = ApplicationStatus.REVIEWING,
        coverLetter = "Với kinh nghiệm về PyTorch và MLOps, tôi tự tin...",
        createdAt = "2025-01-20T11:00:00",
        updatedAt = "2025-01-21T16:00:00",
        job = JobSummary("6", "Machine Learning Engineer", 5500, "Remote"),
        company = CompanySummary("c6", "Zalo AI", null),
        user = null
    )
)
