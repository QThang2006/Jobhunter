package com.example.dacs4.data.model

// ═══════════════════════════════════════════════════════════════════
//  COMPANY MODELS — Phase 3
//  Data classes cho Company
// ═══════════════════════════════════════════════════════════════════

data class Company(
    val id: String,
    val name: String,
    val description: String = "",
    val address: String = "",
    val logo: String? = null,
    val scale: CompanyScale = CompanyScale.MEDIUM,
    val industry: String = "",
    val website: String = "",
    val foundedYear: Int? = null,
    val totalJobs: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val isFeatured: Boolean = false,
    val benefits: List<String> = emptyList(),
    val techStack: List<String> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class CompanyScale(val displayName: String, val range: String) {
    STARTUP("Startup",    "1–50 nhân viên"),
    SMALL("Nhỏ",          "50–200 nhân viên"),
    MEDIUM("Vừa",         "200–500 nhân viên"),
    LARGE("Lớn",          "500–1000 nhân viên"),
    ENTERPRISE("Tập đoàn","1000+ nhân viên")
}

data class CompanyListResponse(
    val meta: PaginationMeta,     // reuse từ ApplicationModels.kt
    val result: List<Company>
)

data class CompanyFilter(
    val industry: String? = null,
    val scale: CompanyScale? = null,
    val query: String = ""
)

// ──────────────────────────────────────────────────────────────────
//  MOCK DATA
// ──────────────────────────────────────────────────────────────────

val mockCompanies = listOf(
    Company(
        id = "c1", name = "VNG Corporation", address = "TP. Hồ Chí Minh",
        description = "Tập đoàn công nghệ hàng đầu Việt Nam với hệ sinh thái sản phẩm đa dạng từ game, nhạc số đến thanh toán điện tử và điện toán đám mây.",
        scale = CompanyScale.ENTERPRISE, industry = "Technology", foundedYear = 2004,
        totalJobs = 24, rating = 4.5f, reviewCount = 312,
        isVerified = true, isFeatured = true,
        techStack = listOf("Kotlin", "Java", "Go", "Kubernetes", "Kafka"),
        benefits  = listOf("Thưởng KPI hấp dẫn", "Bảo hiểm Premium", "Budget học tập")
    ),
    Company(
        id = "c2", name = "Tiki", address = "Hà Nội & TP. HCM",
        description = "Nền tảng thương mại điện tử hàng đầu Việt Nam, xây dựng trải nghiệm mua sắm tốt nhất cho hàng triệu người dùng.",
        scale = CompanyScale.LARGE, industry = "E-Commerce", foundedYear = 2010,
        totalJobs = 18, rating = 4.2f, reviewCount = 241,
        isVerified = true, isFeatured = true,
        techStack = listOf("React", "Node.js", "Python", "PostgreSQL", "Redis"),
        benefits  = listOf("Remote hybrid", "Flexible hours", "Free lunch")
    ),
    Company(
        id = "c3", name = "Momo", address = "TP. Hồ Chí Minh",
        description = "Ví điện tử số 1 Việt Nam, tiên phong trong lĩnh vực Fintech với hơn 40 triệu người dùng.",
        scale = CompanyScale.LARGE, industry = "Fintech", foundedYear = 2013,
        totalJobs = 15, rating = 4.3f, reviewCount = 198,
        isVerified = true, isFeatured = false,
        techStack = listOf("iOS", "Android", "Swift", "Kotlin", "Java Spring"),
        benefits  = listOf("13th month salary", "Stock options", "Health insurance")
    ),
    Company(
        id = "c4", name = "FPT Software", address = "Đà Nẵng & Hà Nội",
        description = "Công ty phần mềm xuất khẩu hàng đầu Việt Nam, cung cấp dịch vụ CNTT cho hơn 30 quốc gia trên thế giới.",
        scale = CompanyScale.ENTERPRISE, industry = "IT Services", foundedYear = 1999,
        totalJobs = 42, rating = 3.9f, reviewCount = 587,
        isVerified = true, isFeatured = false,
        techStack = listOf("Java", ".NET", "SAP", "Salesforce", "AWS"),
        benefits  = listOf("Overseas opportunities", "Certification budget", "Company trips")
    ),
    Company(
        id = "c5", name = "Shopee Vietnam", address = "TP. Hồ Chí Minh",
        description = "Nền tảng thương mại điện tử hàng đầu Đông Nam Á, mang đến trải nghiệm mua sắm online tốt nhất.",
        scale = CompanyScale.ENTERPRISE, industry = "E-Commerce", foundedYear = 2015,
        totalJobs = 31, rating = 4.1f, reviewCount = 445,
        isVerified = true, isFeatured = true,
        techStack = listOf("Go", "Python", "Kubernetes", "Kafka", "MySQL"),
        benefits  = listOf("Competitive salary", "SEA travel", "Free meals")
    ),
    Company(
        id = "c6", name = "Zalo AI", address = "TP. Hồ Chí Minh",
        description = "Trung tâm AI của VNG, nghiên cứu và phát triển các giải pháp trí tuệ nhân tạo cho thị trường Việt Nam và quốc tế.",
        scale = CompanyScale.MEDIUM, industry = "AI / Research", foundedYear = 2017,
        totalJobs = 9, rating = 4.7f, reviewCount = 87,
        isVerified = true, isFeatured = true,
        techStack = listOf("Python", "PyTorch", "TensorFlow", "CUDA", "Kubernetes"),
        benefits  = listOf("Research budget", "Conference sponsorship", "Remote friendly")
    ),
    Company(
        id = "c7", name = "Grab Vietnam", address = "TP. Hồ Chí Minh",
        description = "Siêu ứng dụng hàng đầu Đông Nam Á cung cấp dịch vụ giao thông, giao đồ ăn và thanh toán điện tử.",
        scale = CompanyScale.ENTERPRISE, industry = "Super App", foundedYear = 2012,
        totalJobs = 27, rating = 4.0f, reviewCount = 322,
        isVerified = true, isFeatured = false,
        techStack = listOf("Go", "Kotlin", "Swift", "Kafka", "PostgreSQL"),
        benefits  = listOf("GrabForWork credits", "International exposure", "Gym allowance")
    ),
    Company(
        id = "c8", name = "NashTech", address = "Đà Nẵng",
        description = "Công ty công nghệ thuộc tập đoàn Nash Squared (Anh Quốc), chuyên cung cấp dịch vụ phát triển phần mềm.",
        scale = CompanyScale.LARGE, industry = "IT Outsourcing", foundedYear = 2000,
        totalJobs = 19, rating = 4.2f, reviewCount = 203,
        isVerified = true, isFeatured = false,
        techStack = listOf("React", "Angular", ".NET", "Azure", "DevOps"),
        benefits  = listOf("UK working style", "Agile environment", "English training")
    )
)

val industryList = listOf(
    "Tất cả", "Technology", "E-Commerce", "Fintech",
    "AI / Research", "IT Services", "IT Outsourcing", "Super App", "Gaming"
)
