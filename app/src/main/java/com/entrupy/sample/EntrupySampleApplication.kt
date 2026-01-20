package com.entrupy.sample

import android.app.Application
import com.entrupy.sdk.app.EntrupyApp

/**
 * Application class that initializes the Entrupy SDK.
 * 
 * The SDK must be initialized in the Application's onCreate() method
 * before any SDK operations can be performed.
 */
class EntrupySampleApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the Entrupy SDK
        // This MUST be called before any SDK operations
        EntrupyApp.init(this)
    }
}

