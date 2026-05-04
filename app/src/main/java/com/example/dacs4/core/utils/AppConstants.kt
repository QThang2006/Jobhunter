package com.example.dacs4.core.utils

import java.text.NumberFormat
import java.util.Locale

object AppConstants {

    const val IMAGE_BASE_URL = "http://10.0.2.2:8080/storage/company/"
    const val RESUME_BASE_URL = "http://10.0.2.2:8080/storage/resume/"

    val LOCATION_LIST: List<Pair<String, String>> = listOf(
        Pair("HANOI", "Hà Nội"),
        Pair("HOCHIMINH", "Hồ Chí Minh"),
        Pair("DANANG", "Đà Nẵng"),
        Pair("OTHER", "Khác")
    )

    private val locationMap: Map<String, String> by lazy {
        LOCATION_LIST.toMap()
    }

    /** Map raw backend code "HANOI" → "Hà Nội". Returns original if not found. */
    fun formatLocation(code: String?): String {
        if (code.isNullOrBlank()) return "Không xác định"
        return locationMap[code.uppercase()] ?: code
    }

    fun formatSalary(salary: Double): String {
        return if (salary >= 1_000_000) {
            val millions = salary / 1_000_000
            // Use US locale to force dot separator: "5.5 M VNĐ" not "5,5 M VNĐ"
            val fmt = NumberFormat.getInstance(Locale.US).apply {
                maximumFractionDigits = 1
                minimumFractionDigits = 0
            }.format(millions)
            "$fmt M VNĐ"
        } else {
            NumberFormat.getInstance(Locale.US).format(salary.toLong()) + " VNĐ"
        }
    }
}
