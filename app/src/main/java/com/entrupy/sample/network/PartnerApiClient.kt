package com.entrupy.sample.network

import com.entrupy.sample.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Simple API client for partner backend communication.
 * 
 * The backend URL is configured via local.properties (partner.backend.url).
 * A sample backend is provided as default for demonstration purposes.
 */
object PartnerApiClient {
    
    /**
     * Check if a valid backend URL is configured.
     * Returns true if the URL is not empty and looks valid.
     */
    val isConfigured: Boolean
        get() = BuildConfig.PARTNER_BACKEND_URL.isNotBlank() && 
                BuildConfig.PARTNER_BACKEND_URL.startsWith("http")
    
    /**
     * The configured backend URL (for display purposes).
     */
    val backendUrl: String
        get() = BuildConfig.PARTNER_BACKEND_URL
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }
    
    private val retrofit: Retrofit by lazy {
        val baseUrl = BuildConfig.PARTNER_BACKEND_URL.ifBlank { 
            // Fallback to a placeholder - will fail gracefully at runtime
            "https://not-configured.example.com/" 
        }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val api: PartnerApi by lazy {
        retrofit.create(PartnerApi::class.java)
    }
}

