package com.drew.qrqu.data

import retrofit2.http.Body
import retrofit2.http.POST

// Data class untuk request body
data class UploadRequest(val qrContent: String)

// Data class untuk response
data class UploadResponse(val id: Int) // Menyesuaikan dengan response jsonplaceholder dummy

interface QrApiService {
    @POST("posts")
    suspend fun uploadQrData(@Body request: UploadRequest): UploadResponse
}
