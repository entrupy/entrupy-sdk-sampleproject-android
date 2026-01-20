# Entrupy SDK Sample Project - Android

This sample project demonstrates how to integrate the Entrupy SDK into an Android application.

## Quick Start - See the Integration Diff

**To see exactly what changes are needed to integrate the Entrupy SDK**, view the `Entrupy SDK Integration` commit:

```bash
git show HEAD --stat
git diff HEAD~1..HEAD
```

This commit contains **only** the SDK integration changes, making it easy to understand what you need to add to your own project.

## Prerequisites

- Android Studio Arctic Fox (2020.3.1) or later
- JDK 11 or later
- Android SDK with API level 24 or higher
- GitHub account with access to Entrupy SDK repository

## Getting Started

### 1. Configure GitHub Packages Authentication

Add your GitHub credentials to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.token=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
```

Or set environment variables:

```bash
export GITHUB_USER=YOUR_GITHUB_USERNAME
export GITHUB_TOKEN=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
```

> **Note:** Your GitHub token needs `read:packages` scope.

### How to Generate a GitHub Personal Access Token

1. **Sign in to GitHub**  
   Go to [github.com](https://github.com) and log in to your account.

2. **Navigate to Token Settings**  
   - Click your profile picture in the top-right corner
   - Select **Settings**
   - Scroll down and click **Developer settings** in the left sidebar
   - Click **Personal access tokens** → **Tokens (classic)**
   - Or go directly to: [https://github.com/settings/tokens](https://github.com/settings/tokens)

3. **Generate a New Token**  
   - Click **Generate new token** → **Generate new token (classic)**
   - You may be prompted to confirm your password

4. **Configure the Token**  
   - **Note**: Enter a descriptive name (e.g., "Entrupy SDK Access")
   - **Expiration**: Choose an expiration period (or "No expiration" for development)
   - **Scopes**: Check the `read:packages` scope (under "write:packages")

5. **Create and Copy the Token**  
   - Click **Generate token** at the bottom
   - **Important**: Copy the token immediately! You won't be able to see it again.
   - Store it securely in your `~/.gradle/gradle.properties` file or as an environment variable

### 2. Configure Partner Backend (Optional for Demo)

The app is pre-configured with Entrupy's sample backend for demonstration. **You can run the app immediately without any configuration.**

To use your own partner backend, add these to `local.properties` (gitignored):

```properties
# Partner Backend Configuration (optional - sample backend used if not set)
partner.backend.url=https://your-partner-backend.example.com/
partner.username=your_username
partner.password=your_password
```

| Configuration | Default Behavior |
|---------------|------------------|
| `partner.backend.url` | Uses Entrupy sample backend |
| `partner.username` | Empty - enter in app UI |
| `partner.password` | Empty - enter in app UI |

> **Note:** The app will show an info banner indicating which backend is being used.

### 3. Clone and Build

```bash
git clone https://github.com/entrupy/entrupy-sdk-sampleproject-android.git
cd entrupy-sdk-sampleproject-android
./gradlew assembleDebug
```

## SDK Integration Guide

The project has two commits to help you understand the integration:

| Commit | Description |
|--------|-------------|
| `Git Init` | Base Android application with UI/theming (not SDK-related) |
| `Entrupy SDK Integration` | **All SDK integration changes** - review this commit! |

### Key Integration Steps

#### 1. Add GitHub Packages Repository (`settings.gradle.kts`)

```kotlin
maven {
    url = uri("https://maven.pkg.github.com/entrupy/entrupy-sdk-android")
    credentials {
        username = providers.gradleProperty("gpr.user").orNull
            ?: providers.environmentVariable("GITHUB_USER").orNull ?: ""
        password = providers.gradleProperty("gpr.token").orNull
            ?: providers.environmentVariable("GITHUB_TOKEN").orNull ?: ""
    }
}
```

#### 2. Add SDK Dependency (`app/build.gradle.kts`)

```kotlin
dependencies {
    implementation("com.entrupy:sdk:1.0.0")
}
```

#### 3. Add Required Permissions (`AndroidManifest.xml`)

```xml
<uses-feature android:name="android.hardware.camera.any" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

#### 4. Initialize SDK (`Application` class)

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EntrupyApp.init(this)
    }
}
```

#### 5. Implement Authorization Flow

```kotlin
// 1. Generate authorization request
val authRequest = EntrupyApp.sharedInstance().generateSDKAuthorizationRequest()

// 2. Send to your backend for signing (your backend calls Entrupy API)
val signedRequest = yourBackend.signRequest(authRequest)

// 3. Login with signed request
EntrupyApp.sharedInstance().loginUser(signedRequest, callback)
```

#### 6. Start Capture Flow

```kotlin
// Build metadata for the item to authenticate
val metadata = buildMap<String, Any?> {
    put(METADATA_KEY_PRODUCT_CATEGORY, "apparel")  // or "luxury", "sneakers"
    put(METADATA_KEY_BRAND, "bape")
    put(METADATA_KEY_ITEM_TYPE, "outerwear")       // for apparel
    put(METADATA_KEY_CUSTOMER_ITEM_ID, "SKU-001")
}

// Start capture
EntrupyApp.sharedInstance().startCapture(
    configMetadata = metadata,
    callback = object : CaptureCallback {
        override fun onCaptureStarted() { /* Capture UI opened */ }
        override fun onCaptureError(errorCode: Int, description: String) { /* Handle error */ }
    }
)
```

### ConfigMetadata Reference

The SDK uses a flexible metadata dictionary to configure the capture flow. Below is a complete reference of all available keys.

#### All Available Metadata Keys

| Key | Constant | Description | Example Values |
|-----|----------|-------------|----------------|
| `product_category` | `METADATA_KEY_PRODUCT_CATEGORY` | **Required.** Category selection | `"luxury"`, `"sneakers"`, `"apparel"` |
| `brand` | `METADATA_KEY_BRAND` | **Required.** Brand name | `"louis vuitton"`, `"nike"`, `"bape"` |
| `material` | `METADATA_KEY_MATERIAL` | Material type (Luxury) | `"monogram canvas"`, `"epi leather"`, `"damier ebene"` |
| `style_name` | `METADATA_KEY_STYLE_NAME` | Product/series name (Sneakers) | `"air jordan 1 retro high"`, `"yeezy boost 350"` |
| `style_code` | `METADATA_KEY_STYLE_CODE` | Style/SKU code (Sneakers) | `"DO7097-100"`, `"FY2903"` |
| `us_size` | `METADATA_KEY_US_SIZE` | US shoe size (Sneakers) | `"9.5"`, `"10"`, `"11.5"` |
| `item_type` | `METADATA_KEY_ITEM_TYPE` | Garment type (Apparel) | `"outerwear"`, `"tops"`, `"bottoms"`, `"hats"` |
| `customer_item_id` | `METADATA_KEY_CUSTOMER_ITEM_ID` | Your internal SKU/ID | `"SKU-12345"`, `"INV-2024-001"` |
| `upc` | `METADATA_KEY_UPC` | UPC barcode | `"194956623456"` |
| `mode_id` | `METADATA_KEY_MODE_ID` | Operation mode | `"add"`, `"check"` |

#### Category-Specific Examples

**Luxury (Handbags, Accessories)**
```kotlin
val luxuryMetadata = configMetadataOf(
    METADATA_KEY_PRODUCT_CATEGORY to "luxury",
    METADATA_KEY_BRAND to "louis vuitton",
    METADATA_KEY_MATERIAL to "monogram canvas",      // optional
    METADATA_KEY_CUSTOMER_ITEM_ID to "LV-NF-001"     // optional
)
```

**Sneakers**
```kotlin
val sneakersMetadata = configMetadataOf(
    METADATA_KEY_PRODUCT_CATEGORY to "sneakers",
    METADATA_KEY_BRAND to "nike",
    METADATA_KEY_STYLE_NAME to "air jordan 1 retro high",  // optional
    METADATA_KEY_STYLE_CODE to "DO7097-100",               // optional
    METADATA_KEY_US_SIZE to "9.5",                         // optional
    METADATA_KEY_CUSTOMER_ITEM_ID to "AJ1-001"             // optional
)
```

**Apparel (Streetwear, Clothing)**
```kotlin
val apparelMetadata = configMetadataOf(
    METADATA_KEY_PRODUCT_CATEGORY to "apparel",
    METADATA_KEY_BRAND to "bape",
    METADATA_KEY_ITEM_TYPE to "outerwear",           // required for apparel
    METADATA_KEY_CUSTOMER_ITEM_ID to "BAPE-001"      // optional
)
```

#### Supported Brands (Examples)

| Category | Example Brands |
|----------|----------------|
| **Luxury** | Louis Vuitton, Gucci, Chanel, Hermès, Prada, Dior, Fendi, Balenciaga |
| **Sneakers** | Nike, Adidas, New Balance, Jordan, Yeezy, Converse |
| **Apparel** | BAPE, Supreme, Off-White, Kith, Palace, Stüssy |

#### Apparel Item Types

| Item Type | Description |
|-----------|-------------|
| `outerwear` | Jackets, coats, hoodies |
| `tops` | T-shirts, shirts, sweaters |
| `bottoms` | Pants, shorts, jeans |
| `hats` | Caps, beanies, bucket hats |

## Project Structure

```
app/src/main/java/com/entrupy/sample/
├── EntrupySampleApplication.kt  # SDK initialization
├── MainActivity.kt              # Main entry point
├── network/                     # Partner backend API
│   ├── PartnerApi.kt
│   └── PartnerApiClient.kt
└── ui/
    ├── screens/
    │   └── MainScreen.kt        # SDK integration demo
    └── theme/
```

## License

Copyright © Entrupy Inc. All rights reserved.
