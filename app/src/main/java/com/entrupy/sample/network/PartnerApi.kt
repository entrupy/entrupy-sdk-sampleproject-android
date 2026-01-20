package com.entrupy.sample.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Partner Backend API for SDK authorization flow.
 * 
 * This API communicates with your backend server which handles:
 * 1. User authentication (login)
 * 2. Signing SDK authorization requests via Entrupy API
 */
interface PartnerApi {
    
    /**
     * Login to partner backend to get an access token.
     */
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    /**
     * Send SDK authorization request to be signed by your backend.
     * Your backend will call Entrupy's API to sign this request.
     */
    @POST("authorize-entrupy-sdk-user")
    suspend fun authorizeSDKUser(
        @Header("Authorization") token: String,
        @Body request: AuthorizationRequest
    ): AuthorizationResponse
}

// Request/Response models

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String?
)

data class AuthorizationRequest(
    val sdk_authorization_request: String,
    val email: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val unique_user_id: String? = null
)

data class AuthorizationResponse(
    val signed_authorization_request: String?
)

