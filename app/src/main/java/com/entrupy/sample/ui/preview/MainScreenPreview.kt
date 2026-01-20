package com.entrupy.sample.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entrupy.sample.ui.theme.*

/**
 * Preview file for MainScreen composables.
 * 
 * This file contains preview-only composables that mirror the private composables
 * in MainScreen.kt for Android Studio preview purposes.
 */

// =============================================================================
// Preview Composables (duplicated for preview visibility)
// =============================================================================

@Composable
private fun PreviewEntrupyCard(
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

@Composable
private fun PreviewAuthorizationStatusCard(
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

@Composable
private fun PreviewConfigurationInfoCard(
    title: String,
    message: String,
    isWarning: Boolean
) {
    val backgroundColor = if (isWarning) 
        Color(0xFF4A3A00).copy(alpha = 0.5f)
    else 
        Color(0xFF1A3A4A).copy(alpha = 0.5f)
    
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

@Composable
private fun PreviewEntrupyButton(
    text: String,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val shape = RoundedCornerShape(6.dp)
    val backgroundBrush = if (enabled) {
        androidx.compose.ui.graphics.Brush.verticalGradient(listOf(AccentGold, AccentGoldDark))
    } else {
        androidx.compose.ui.graphics.Brush.verticalGradient(listOf(ButtonDisabledBackground, ButtonDisabledBackground.copy(alpha = 0.8f)))
    }
    val contentColor = if (enabled) Color.Black else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backgroundBrush, shape),
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
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = contentColor)
                Spacer(modifier = Modifier.width(8.dp))
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

@Composable
private fun entrupyTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentGold,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = AccentGold,
    cursorColor = AccentGold
)

// =============================================================================
// Previews
// =============================================================================

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun PreviewMainScreenNotAuthorized() {
    EntrupySampleTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
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
            
            // Sample Backend Info Card
            PreviewConfigurationInfoCard(
                title = "Using Sample Backend",
                message = "This app is configured to use Entrupy's sample backend for demonstration.\n\nTo use your own backend, add to local.properties:\n• partner.backend.url\n• partner.username\n• partner.password",
                isWarning = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Authorization Status
            PreviewAuthorizationStatusCard(
                isAuthorized = false,
                statusMessage = "Ready to integrate with Entrupy SDK"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login Card
            PreviewEntrupyCard(
                title = "Partner Credentials",
                description = "Enter your partner backend credentials"
            ) {
                OutlinedTextField(
                    value = "user1",
                    onValueChange = {},
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "••••••",
                    onValueChange = {},
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                PreviewEntrupyButton(text = "Login & Authorize")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun PreviewMainScreenAuthorizedLuxury() {
    EntrupySampleTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
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
            
            // Authorization Status - Authorized
            PreviewAuthorizationStatusCard(
                isAuthorized = true,
                statusMessage = "Login successful! Ready to capture."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Capture Configuration - Luxury
            PreviewEntrupyCard(
                title = "Capture Configuration",
                description = "Configure the item to authenticate"
            ) {
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
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Luxury", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGold.copy(alpha = 0.2f),
                            selectedLabelColor = AccentGold
                        )
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Sneakers", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Apparel", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = "louis vuitton",
                    onValueChange = {},
                    label = { Text("Brand") },
                    placeholder = { Text("e.g., louis vuitton, gucci, chanel") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "monogram canvas",
                    onValueChange = {},
                    label = { Text("Material") },
                    placeholder = { Text("e.g., monogram canvas, epi leather") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "LV-NF-001",
                    onValueChange = {},
                    label = { Text("Customer Item ID") },
                    placeholder = { Text("Your internal SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                PreviewEntrupyButton(text = "Start Capture")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun PreviewMainScreenAuthorizedSneakers() {
    EntrupySampleTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
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
            
            // Authorization Status - Authorized
            PreviewAuthorizationStatusCard(
                isAuthorized = true,
                statusMessage = "Login successful! Ready to capture."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Capture Configuration - Sneakers
            PreviewEntrupyCard(
                title = "Capture Configuration",
                description = "Configure the item to authenticate"
            ) {
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
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Luxury", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Sneakers", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGold.copy(alpha = 0.2f),
                            selectedLabelColor = AccentGold
                        )
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Apparel", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = "nike",
                    onValueChange = {},
                    label = { Text("Brand") },
                    placeholder = { Text("e.g., nike, adidas, new balance") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "air jordan 1 retro high",
                    onValueChange = {},
                    label = { Text("Style Name") },
                    placeholder = { Text("e.g., air jordan 1 retro high") },
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
                        value = "DO7097-100",
                        onValueChange = {},
                        label = { Text("Style Code") },
                        placeholder = { Text("DO7097-100") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = entrupyTextFieldColors()
                    )
                    
                    OutlinedTextField(
                        value = "9.5",
                        onValueChange = {},
                        label = { Text("US Size") },
                        placeholder = { Text("9.5") },
                        modifier = Modifier.weight(0.6f),
                        singleLine = true,
                        colors = entrupyTextFieldColors()
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "AJ1-001",
                    onValueChange = {},
                    label = { Text("Customer Item ID") },
                    placeholder = { Text("Your internal SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                PreviewEntrupyButton(text = "Start Capture")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun PreviewMainScreenAuthorizedApparel() {
    EntrupySampleTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
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
            
            // Authorization Status - Authorized
            PreviewAuthorizationStatusCard(
                isAuthorized = true,
                statusMessage = "Login successful! Ready to capture."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Capture Configuration - Apparel
            PreviewEntrupyCard(
                title = "Capture Configuration",
                description = "Configure the item to authenticate"
            ) {
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
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Luxury", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Sneakers", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text("Apparel", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGold.copy(alpha = 0.2f),
                            selectedLabelColor = AccentGold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = "bape",
                    onValueChange = {},
                    label = { Text("Brand") },
                    placeholder = { Text("e.g., bape, supreme, off-white") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "outerwear",
                    onValueChange = {},
                    label = { Text("Item Type") },
                    placeholder = { Text("e.g., outerwear, tops, bottoms, hats") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = "BAPE-001",
                    onValueChange = {},
                    label = { Text("Customer Item ID") },
                    placeholder = { Text("Your internal SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = entrupyTextFieldColors()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                PreviewEntrupyButton(text = "Start Capture")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun PreviewConfigurationWarningCard() {
    EntrupySampleTheme {
        Column(
            modifier = Modifier
                .background(Background)
                .padding(20.dp)
        ) {
            PreviewConfigurationInfoCard(
                title = "Backend Not Configured",
                message = "Please configure your partner backend in local.properties:\n\npartner.backend.url=https://your-backend.com/\npartner.username=your_user\npartner.password=your_pass",
                isWarning = true
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0E17)
@Composable
private fun PreviewButtonStates() {
    EntrupySampleTheme {
        Column(
            modifier = Modifier
                .background(Background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Button States", color = Color.White, fontWeight = FontWeight.Bold)
            
            PreviewEntrupyButton(text = "Enabled Button", enabled = true)
            PreviewEntrupyButton(text = "Disabled Button", enabled = false)
            PreviewEntrupyButton(text = "Loading...", enabled = true, isLoading = true)
        }
    }
}

