package com.entrupy.sample.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrupy.sample.BuildConfig
import com.entrupy.sample.network.AuthorizationRequest
import com.entrupy.sample.network.LoginRequest
import com.entrupy.sample.network.PartnerApiClient
import com.entrupy.sample.ui.theme.AccentGold
import com.entrupy.sample.ui.theme.AccentGoldDark
import com.entrupy.sample.ui.theme.AccentGreen
import com.entrupy.sample.ui.theme.Background
import com.entrupy.sample.ui.theme.ButtonDisabledBackground
import com.entrupy.sample.ui.theme.CardBackground
import com.entrupy.sdk.app.EntrupyApp
import com.entrupy.sdk.listeners.CaptureCallback
import com.entrupy.sdk.listeners.EntrupyErrorCode
import com.entrupy.sdk.listeners.SdkLoginCallback
import com.entrupy.sdk.model.METADATA_KEY_BRAND
import com.entrupy.sdk.model.METADATA_KEY_CUSTOMER_ITEM_ID
import com.entrupy.sdk.model.METADATA_KEY_ITEM_TYPE
import com.entrupy.sdk.model.METADATA_KEY_MATERIAL
import com.entrupy.sdk.model.METADATA_KEY_PRODUCT_CATEGORY
import com.entrupy.sdk.model.METADATA_KEY_STYLE_CODE
import com.entrupy.sdk.model.METADATA_KEY_STYLE_NAME
import com.entrupy.sdk.model.METADATA_KEY_US_SIZE
import com.entrupy.sdk.model.ProductCategory
import kotlinx.coroutines.launch

private const val TAG = "EntrupySampleApp"

/**
 * Main screen demonstrating the complete Entrupy SDK integration flow:
 * 
 * 1. Generate SDK authorization request
 * 2. Login to partner backend
 * 3. Send auth request to partner backend for signing
 * 4. Login to SDK with signed request
 * 5. Start capture flow
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val entrupyApp = remember { EntrupyApp.sharedInstance() }
    val scope = rememberCoroutineScope()
    
    // State
    var isAuthorized by remember { mutableStateOf(entrupyApp.isAuthorizationValid()) }
    var statusMessage by remember { mutableStateOf("Ready to integrate with Entrupy SDK") }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Partner credentials - loaded from BuildConfig (local.properties) with UI fallback
    var partnerUsername by remember { mutableStateOf(BuildConfig.PARTNER_USERNAME) }
    var partnerPassword by remember { mutableStateOf(BuildConfig.PARTNER_PASSWORD) }
    
    // Check if credentials were pre-configured (from local.properties)
    val hasPreConfiguredCredentials = remember {
        BuildConfig.PARTNER_USERNAME.isNotBlank() && BuildConfig.PARTNER_PASSWORD.isNotBlank()
    }
    
    // Check if backend is properly configured
    val isBackendConfigured = remember { PartnerApiClient.isConfigured }
    val isSampleBackend = remember { 
        PartnerApiClient.backendUrl.contains("sample-partner-sdk-server.entrupy.com") 
    }
    
    // Capture configuration
    var selectedCategoryIndex by remember { mutableIntStateOf(2) } // Default to Apparel
    var brandId by remember { mutableStateOf("bape") }
    
    // Category-specific fields with defaults
    // Luxury: material (e.g., "monogram canvas", "epi leather", "damier ebene")
    var material by remember { mutableStateOf("monogram canvas") }
    
    // Sneakers: style_name, style_code, us_size
    var styleName by remember { mutableStateOf("air jordan 1 retro high") }
    var styleCode by remember { mutableStateOf("") }  // e.g., "DO7097-100"
    var usSize by remember { mutableStateOf("") }     // e.g., "9.5"
    
    // Apparel: item_type (e.g., "outerwear", "tops", "bottoms", "hats")
    var itemType by remember { mutableStateOf("outerwear") }
    
    var customerItemId by remember { mutableStateOf("SAMPLE-ITEM-001") }
    var isCaptureLoading by remember { mutableStateOf(false) }
    
    val categories = remember { ProductCategory.all() }
    
    // Update defaults when category changes
    LaunchedEffect(selectedCategoryIndex) {
        when (categories[selectedCategoryIndex]) {
            ProductCategory.Luxury -> {
                brandId = "louis vuitton"
                material = "monogram canvas"
            }
            ProductCategory.Sneakers -> {
                brandId = "nike"
                styleName = "air jordan 1 retro high"
                styleCode = ""
                usSize = ""
            }
            ProductCategory.Apparel -> {
                brandId = "bape"
                itemType = "outerwear"
            }
        }
    }
    
    /**
     * Complete login flow
     */
    fun performFullLogin() {
        scope.launch {
            isLoading = true
            try {
                // Step 1: Generate SDK authorization request
                statusMessage = "Step 1/4: Generating auth request..."
                Log.d(TAG, "Step 1: Generating SDK authorization request")
                val authRequest = entrupyApp.generateSDKAuthorizationRequest()
                Log.d(TAG, "Auth request generated: ${authRequest.take(50)}...")
                
                // Step 2: Login to partner backend
                statusMessage = "Step 2/4: Logging into partner backend..."
                Log.d(TAG, "Step 2: Logging into partner backend")
                val loginResponse = PartnerApiClient.api.login(
                    LoginRequest(username = partnerUsername, password = partnerPassword)
                )
                val partnerToken = loginResponse.token 
                    ?: throw Exception("Partner login failed: no token received")
                Log.d(TAG, "Partner login successful")
                
                // Step 3: Get signed authorization from partner backend
                statusMessage = "Step 3/4: Getting signed authorization..."
                Log.d(TAG, "Step 3: Getting signed authorization from partner")
                val authResponse = PartnerApiClient.api.authorizeSDKUser(
                    token = "Token $partnerToken",
                    request = AuthorizationRequest(
                        sdk_authorization_request = authRequest,
                    )
                )
                val signedRequest = authResponse.signed_authorization_request
                    ?: throw Exception("Partner authorization failed: no signed request received")
                Log.d(TAG, "Signed request received: ${signedRequest.take(50)}...")
                
                // Step 4: Login to SDK with signed request
                statusMessage = "Step 4/4: Logging into SDK..."
                Log.d(TAG, "Step 4: Logging into SDK with signed request")
                entrupyApp.loginUser(
                    signedRequest = signedRequest,
                    callback = object : SdkLoginCallback {
                        override fun onLoginStarted() {
                            Log.d(TAG, "SDK login started")
                        }
                        
                        override fun onLoginSuccess(expirationTime: Long) {
                            isLoading = false
                            isAuthorized = true
                            statusMessage = "Login successful! Ready to capture."
                            Log.d(TAG, "SDK login success, expires: $expirationTime")
                        }
                        
                        override fun onLoginError(
                            errorCode: Int,
                            description: String,
                            localizedDescription: String
                        ) {
                            isLoading = false
                            isAuthorized = false
                            statusMessage = "SDK login failed"
                            errorMessage = localizedDescription
                            showError = true
                            Log.e(TAG, "SDK login error: $description (Code: $errorCode)")
                        }
                    }
                )
                
            } catch (e: Exception) {
                isLoading = false
                statusMessage = "Login failed"
                errorMessage = e.message ?: "Unknown error"
                showError = true
                Log.e(TAG, "Login flow error", e)
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header with Entrupy branding
        Text(
            text = "entrupy",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = AccentGold
        )
        
        Text(
            text = "SDK Sample",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Configuration Info Card (when using sample backend or missing config)
        if (isSampleBackend && !isAuthorized) {
            ConfigurationInfoCard(
                title = "Using Sample Backend",
                message = "This app is configured to use Entrupy's sample backend for demonstration.\n\nTo use your own backend, add to local.properties:\n• partner.backend.url\n• partner.username\n• partner.password",
                isWarning = false
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else if (!isBackendConfigured && !isAuthorized) {
            ConfigurationInfoCard(
                title = "Backend Not Configured",
                message = "Please configure your partner backend in local.properties:\n\npartner.backend.url=https://your-backend.com/\npartner.username=your_user\npartner.password=your_pass",
                isWarning = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Authorization Status Card
        AuthorizationStatusCard(
            isAuthorized = isAuthorized,
            statusMessage = statusMessage
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login Section
        if (!isAuthorized) {
            EntrupyCard(
                title = "Partner Credentials",
                description = if (hasPreConfiguredCredentials) 
                    "Credentials loaded from local.properties" 
                else 
                    "Enter your partner backend credentials"
            ) {
                OutlinedTextField(
                    value = partnerUsername,
                    onValueChange = { partnerUsername = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors(),
                    supportingText = if (hasPreConfiguredCredentials) {
                        { Text("Pre-configured", color = AccentGreen) }
                    } else null
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = partnerPassword,
                    onValueChange = { partnerPassword = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors(),
                    supportingText = if (hasPreConfiguredCredentials) {
                        { Text("Pre-configured", color = AccentGreen) }
                    } else null
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                EntrupyButton(
                    text = if (isLoading) statusMessage else "Login & Authorize",
                    onClick = { performFullLogin() },
                    enabled = !isLoading && partnerUsername.isNotBlank() && partnerPassword.isNotBlank(),
                    isLoading = isLoading
                )
            }
        }
        
        // Capture Configuration (shown when authorized)
        if (isAuthorized) {
            EntrupyCard(
                title = "Capture Configuration",
                description = "Configure the item to authenticate"
            ) {
                // Product Category Selection
                Text(
                    text = "Product Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEachIndexed { index, category ->
                        FilterChip(
                            selected = selectedCategoryIndex == index,
                            onClick = { selectedCategoryIndex = index },
                            label = { 
                                Text(
                                    category.displayName,
                                    fontSize = 12.sp
                                ) 
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentGold.copy(alpha = 0.2f),
                                selectedLabelColor = AccentGold
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Get selected category
                val selectedCategory = categories[selectedCategoryIndex]
                
                // Brand field with category-specific placeholder
                OutlinedTextField(
                    value = brandId,
                    onValueChange = { brandId = it },
                    label = { Text("Brand") },
                    placeholder = { 
                        Text(
                            when (selectedCategory) {
                                ProductCategory.Luxury -> "e.g., louis vuitton, gucci, chanel"
                                ProductCategory.Sneakers -> "e.g., nike, adidas, new balance"
                                ProductCategory.Apparel -> "e.g., bape, supreme, off-white"
                            }
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Category-specific fields
                when (selectedCategory) {
                    ProductCategory.Luxury -> {
                        OutlinedTextField(
                            value = material,
                            onValueChange = { material = it },
                            label = { Text("Material") },
                            placeholder = { Text("e.g., monogram canvas, epi leather, damier ebene") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = entrupyTextFieldColors()
                        )
                    }
                    ProductCategory.Sneakers -> {
                        // Style Name
                        OutlinedTextField(
                            value = styleName,
                            onValueChange = { styleName = it },
                            label = { Text("Style Name") },
                            placeholder = { Text("e.g., air jordan 1 retro high, yeezy boost 350") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = entrupyTextFieldColors()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Style Code and US Size in a row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = styleCode,
                                onValueChange = { styleCode = it },
                                label = { Text("Style Code") },
                                placeholder = { Text("DO7097-100") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = entrupyTextFieldColors()
                            )
                            
                            OutlinedTextField(
                                value = usSize,
                                onValueChange = { usSize = it },
                                label = { Text("US Size") },
                                placeholder = { Text("9.5") },
                                modifier = Modifier.weight(0.6f),
                                singleLine = true,
                                colors = entrupyTextFieldColors()
                            )
                        }
                    }
                    ProductCategory.Apparel -> {
                        OutlinedTextField(
                            value = itemType,
                            onValueChange = { itemType = it },
                            label = { Text("Item Type") },
                            placeholder = { Text("e.g., outerwear, tops, bottoms, hats") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = entrupyTextFieldColors()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = customerItemId,
                    onValueChange = { customerItemId = it },
                    label = { Text("Customer Item ID") },
                    placeholder = { Text("Your internal SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                EntrupyButton(
                    text = if (isCaptureLoading) "Starting..." else "Start Capture",
                    onClick = {
                        isCaptureLoading = true
                        val category = categories[selectedCategoryIndex]
                        
                        // Build metadata based on category
                        // See README.md for full list of available metadata keys
                        val metadata = buildMap<String, Any?> {
                            put(METADATA_KEY_PRODUCT_CATEGORY, category.displayName.lowercase())
                            put(METADATA_KEY_BRAND, brandId.lowercase().trim())
                            
                            // Category-specific fields
                            when (category) {
                                ProductCategory.Luxury -> {
                                    // Luxury supports: material
                                    if (material.isNotBlank()) {
                                        put(METADATA_KEY_MATERIAL, material.lowercase().trim())
                                    }
                                }
                                ProductCategory.Sneakers -> {
                                    // Sneakers supports: style_name, style_code, us_size
                                    if (styleName.isNotBlank()) {
                                        put(METADATA_KEY_STYLE_NAME, styleName.lowercase().trim())
                                    }
                                    if (styleCode.isNotBlank()) {
                                        put(METADATA_KEY_STYLE_CODE, styleCode.uppercase().trim())
                                    }
                                    if (usSize.isNotBlank()) {
                                        put(METADATA_KEY_US_SIZE, usSize.trim())
                                    }
                                }
                                ProductCategory.Apparel -> {
                                    // Apparel requires: item_type
                                    if (itemType.isNotBlank()) {
                                        put(METADATA_KEY_ITEM_TYPE, itemType.lowercase().trim())
                                    }
                                }
                            }
                            
                            // Include customer_item_id if provided (works for all categories)
                            if (customerItemId.isNotBlank()) {
                                put(METADATA_KEY_CUSTOMER_ITEM_ID, customerItemId)
                            }
                        }
                        
                        entrupyApp.startCapture(
                            configMetadata = metadata,
                            callback = object : CaptureCallback {
                                override fun onCaptureStarted() {
                                    isCaptureLoading = false
                                    statusMessage = "Capture flow started"
                                    Log.d(TAG, "Capture started for brand: $brandId")
                                }
                                
                                override fun onCaptureError(errorCode: Int, description: String) {
                                    isCaptureLoading = false
                                    statusMessage = "Capture failed"
                                    errorMessage = when (errorCode) {
                                        EntrupyErrorCode.NO_MATCHING_CONFIG -> 
                                            "No configuration found for brand '$brandId' in ${category.displayName}"
                                        EntrupyErrorCode.SDK_NOT_INITIALIZED -> 
                                            "SDK not initialized"
                                        EntrupyErrorCode.UNAUTHORIZED_ACCESS -> 
                                            "Authorization expired. Please login again."
                                        else -> description
                                    }
                                    showError = true
                                    Log.e(TAG, "Capture error: $description (Code: $errorCode)")
                                }
                            }
                        )
                    },
                    enabled = brandId.isNotBlank() && !isCaptureLoading,
                    isLoading = isCaptureLoading,
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logout Button
            OutlinedButton(
                onClick = {
                    entrupyApp.cleanup()
                    isAuthorized = false
                    statusMessage = "Logged out. Ready to login again."
                    Log.d(TAG, "User logged out")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error))
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Footer
        Text(
            text = "Powered by Entrupy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    // Error Dialog
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            icon = { Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK", color = AccentGold)
                }
            },
            containerColor = CardBackground
        )
    }
}

/**
 * Entrupy styled button with gold gradient
 */
@Composable
private fun EntrupyButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(6.dp)
    val gradientEnabled = Brush.verticalGradient(
        colors = listOf(AccentGold, AccentGoldDark)
    )
    val gradientDisabled = Brush.verticalGradient(
        colors = listOf(ButtonDisabledBackground, ButtonDisabledBackground.copy(alpha = 0.8f))
    )
    val backgroundBrush = if (enabled) gradientEnabled else gradientDisabled
    val contentColor = if (enabled) Color.Black else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(backgroundBrush)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF11141E),
                trackColor = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon?.invoke()
                if (icon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Authorization status card with Entrupy styling
 */
@Composable
private fun AuthorizationStatusCard(
    isAuthorized: Boolean,
    statusMessage: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAuthorized) 
                AccentGreen.copy(alpha = 0.1f)
            else 
                CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isAuthorized) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isAuthorized) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = if (isAuthorized) "Authorized" else "Not Authorized",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAuthorized) AccentGreen else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Configuration info/warning card
 */
@Composable
private fun ConfigurationInfoCard(
    title: String,
    message: String,
    isWarning: Boolean
) {
    val backgroundColor = if (isWarning) 
        Color(0xFF4A3A00).copy(alpha = 0.5f) // Amber warning
    else 
        Color(0xFF1A3A4A).copy(alpha = 0.5f) // Cyan info
    
    val borderColor = if (isWarning) AccentGold else MaterialTheme.colorScheme.primary
    val iconColor = if (isWarning) AccentGold else MaterialTheme.colorScheme.primary
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isWarning) Icons.Default.Error else Icons.Default.Info,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = iconColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Entrupy styled card
 */
@Composable
private fun EntrupyCard(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            content()
        }
    }
}

/**
 * Entrupy text field colors
 */
@Composable
private fun entrupyTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentGold,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = AccentGold,
    cursorColor = AccentGold
)
