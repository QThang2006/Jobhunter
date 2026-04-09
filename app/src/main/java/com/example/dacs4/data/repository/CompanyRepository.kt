package com.example.dacs4.data.repository

import com.example.dacs4.data.model.Company
import com.example.dacs4.data.model.mockCompanies
import com.example.dacs4.data.remote.CompanyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════════
//  COMPANY REPOSITORY — Phase 3
//  Single source of truth cho dữ liệu Company
// ═══════════════════════════════════════════════════════════════════

@Singleton
class CompanyRepository @Inject constructor(
    private val apiService: CompanyApiService
) {

    // ──────────────────────────────────────────────────────────────
    //  GET COMPANIES (list + filter)
    // ──────────────────────────────────────────────────────────────

    /**
     * Lấy danh sách công ty, hỗ trợ tìm kiếm và lọc theo ngành
     * @param page      Trang hiện tại (mặc định 1)
     * @param query     Từ khoá tìm kiếm (tên công ty, ngành...)
     * @param industry  Lọc theo ngành ("Tất cả" = không lọc)
     */
    suspend fun getCompanies(
        page: Int = 1,
        query: String = "",
        industry: String? = null
    ): Result<List<Company>> = withContext(Dispatchers.IO) {
        runCatching {
            delay(700)
            // Mock: filter trên local data
            mockCompanies.filter { company ->
                val matchQuery = query.isEmpty() ||
                    company.name.contains(query, ignoreCase = true) ||
                    company.industry.contains(query, ignoreCase = true) ||
                    company.address.contains(query, ignoreCase = true)

                val matchIndustry = industry == null ||
                    industry == "Tất cả" ||
                    company.industry == industry

                matchQuery && matchIndustry
            }

            /* REAL API:
            val response = apiService.getCompanies(
                page     = page,
                pageSize = 10,
                name     = query.ifEmpty { null },
                industry = if (industry == "Tất cả") null else industry
            )
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()!!.data!!.result
            } else {
                throw Exception(response.body()?.message ?: "Không thể tải danh sách công ty")
            }
            */
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  GET COMPANY DETAIL
    // ──────────────────────────────────────────────────────────────

    /**
     * Lấy chi tiết một công ty theo ID
     */
    suspend fun getCompanyDetail(id: String): Result<Company> =
        withContext(Dispatchers.IO) {
            runCatching {
                delay(500)
                mockCompanies.find { it.id == id }
                    ?: throw Exception("Không tìm thấy công ty")

                /* REAL API:
                val response = apiService.getCompanyDetail(id)
                if (response.isSuccessful && response.body()?.data != null) {
                    response.body()!!.data!!
                } else {
                    throw Exception(response.body()?.message ?: "Không thể tải thông tin công ty")
                }
                */
            }
        }
}
