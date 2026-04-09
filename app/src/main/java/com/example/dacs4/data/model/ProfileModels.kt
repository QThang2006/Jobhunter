package com.example.dacs4.data.model

// ═══════════════════════════════════════════════════════════════════
//  PROFILE MODELS — Phase 3
//  Data classes cho User Profile
// ═══════════════════════════════════════════════════════════════════

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val address: String = "",
    val age: Int? = null,
    val gender: Gender = Gender.OTHER,
    val avatar: String? = null,
    val role: RoleDetail? = null,
    val company: CompanyRef? = null,
    val skills: List<String> = emptyList(),
    val bio: String = "",
    val linkedIn: String = "",
    val github: String = "",
    val website: String = "",
    val yearsOfExperience: Int = 0,
    val desiredPosition: String = "",
    val desiredSalary: Long? = null,
    val isLookingForJob: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class Gender(val displayName: String) {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác")
}

data class RoleDetail(
    val id: String,
    val name: String
)

data class CompanyRef(
    val id: String,
    val name: String,
    val logo: String?
)

data class UpdateProfileRequest(
    val name: String,
    val phone: String,
    val address: String,
    val age: Int?,
    val gender: String,
    val bio: String,
    val skills: List<String>,
    val linkedIn: String,
    val github: String,
    val website: String,
    val yearsOfExperience: Int,
    val desiredPosition: String,
    val desiredSalary: Long?,
    val isLookingForJob: Boolean
)

// ──────────────────────────────────────────────────────────────────
//  MOCK DATA
// ──────────────────────────────────────────────────────────────────

val mockUserProfile = UserProfile(
    id = "u1",
    name = "Nguyễn Văn An",
    email = "an.nguyen@example.com",
    phone = "0901234567",
    address = "Quận 7, TP. Hồ Chí Minh",
    age = 26,
    gender = Gender.MALE,
    bio = "Senior Android Developer với 4 năm kinh nghiệm. Đam mê xây dựng ứng dụng mobile hiệu suất cao và UI/UX tinh tế.",
    skills = listOf("Kotlin", "Jetpack Compose", "MVVM", "Coroutines", "Room", "Retrofit", "Hilt", "Git"),
    yearsOfExperience = 4,
    desiredPosition = "Senior / Lead Android Developer",
    desiredSalary = 4000,
    isLookingForJob = true,
    linkedIn = "linkedin.com/in/annguyen",
    github = "github.com/annguyen-dev",
    createdAt = "2023-06-15T08:00:00"
)
