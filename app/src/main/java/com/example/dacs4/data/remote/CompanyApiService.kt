package com.example.dacs4.data.remote

import com.example.dacs4.data.model.Company
import com.example.dacs4.data.model.CompanyListResponse
import com.example.dacs4.data.model.response.BaseResponse
import retrofit2.Response
import retrofit2.http.*

// ═══════════════════════════════════════════════════════════════════
//  COMPANY API SERVICE — Phase 3
//  Endpoints liên quan đến công ty
// ═══════════════════════════════════════════════════════════════════

interface CompanyApiService {

    /**
     * Lấy danh sách công ty có phân trang và filter
     * GET /api/v1/companies?current=1&pageSize=10&name=VNG&industry=Technology
     *
     * @param page      Trang hiện tại (bắt đầu từ 1)
     * @param pageSize  Số item mỗi trang (mặc định 10)
     * @param name      Tìm kiếm theo tên công ty (optional)
     * @param industry  Lọc theo ngành nghề (optional)
     */
    @GET("/api/v1/companies")
    suspend fun getCompanies(
        @Query("current")  page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("name")     name: String? = null,
        @Query("industry") industry: String? = null
    ): Response<BaseResponse<CompanyListResponse>>

    /**
     * Lấy chi tiết một công ty theo ID
     * GET /api/v1/companies/{id}
     */
    @GET("/api/v1/companies/{id}")
    suspend fun getCompanyDetail(
        @Path("id") id: String
    ): Response<BaseResponse<Company>>
}
