# Entrupy SDK Sample Project - Android

This sample project demonstrates how to integrate the Entrupy SDK into an Android application.

## Quick Start - Where the SDK Code Lives

All SDK usage is in a few files (see [Project Structure](#project-structure)):

- `EntrupySampleApplication.kt` - `EntrupyApp.init` and the stored SDK theme
- `theme/SampleSdkTheme.kt` - `EntrupyTheme` examples
- `ui/screens/MainScreen.kt` - authorization, login, the SDK Theme picker, and `startCapture` for authentication and fingerprint flows
- `network/` - the partner backend calls that sign the SDK authorization request

The `Entrupy SDK Integration` commit in the Git history shows the original integration against SDK 1.0. Later commits track SDK upgrades, so use the files above as the reference for the current API.

## Prerequisites

- Android Studio Ladybug (2024.2.1) or later
- JDK 11 or later
- Android SDK with API level 24 or higher (the SDK requires `compileSdk` 36)
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
    implementation("com.entrupy:sdk:2.5.0")
}
```

The SDK requires `compileSdk = 36`.

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
val metadata = mapOf<String, Any?>(
    METADATA_KEY_BRAND to "Bape",
    METADATA_KEY_ITEM_TYPE to "Outerwear",
    METADATA_KEY_CUSTOMER_ITEM_ID to "SKU-001",
)

// Start capture
EntrupyApp.sharedInstance().startCapture(
    configMetadata = metadata,
    callback = object : CaptureCallback {
        override fun onCaptureStarted() { /* Capture UI opened */ }
        override fun onCaptureError(errorCode: Int, description: String) { /* Handle error */ }
        override fun onCaptureCompleted(result: CaptureResult) { /* Images submitted */ }
        override fun onCaptureTimeout() { /* Capture session expired */ }
        override fun onFallbackOpened() { /* Brand fallback opened */ }
    },
    showResultScreen = true,
)
```

With `showResultScreen = true` the SDK shows its Result screen after submission. Pass `false` to
return to your app instead: `onCaptureCompleted(result)` then also carries `result.authenticationId`
(the item's Entrupy ID) and `result.status`, and you can open the item with
`EntrupyApp.sharedInstance().displayDetailViewForItem(entrupyId)`.

#### Fingerprint Register and Compare

Fingerprinting must be enabled for your Entrupy account. Always pass a callback when starting a
fingerprint flow; lookup errors are reported through `onCaptureError`.

**Register.** `METADATA_KEY_ITEM_TYPE` and `METADATA_KEY_CUSTOMER_ITEM_ID` are optional. With an item
type, the SDK opens the matching Register flow; without one, it opens the item menu filtered to
Register flows. A Customer Item ID is submitted through a hidden SKU field; when omitted, that field
stays blank.

```kotlin
val registerMetadata = mapOf<String, Any?>(
    METADATA_KEY_CAPTURE_WORKFLOW to CAPTURE_WORKFLOW_FINGERPRINT_REGISTER,
    METADATA_KEY_ITEM_TYPE to "Tops",
    METADATA_KEY_CUSTOMER_ITEM_ID to "SKU-001",
)

EntrupyApp.sharedInstance().startCapture(
    configMetadata = registerMetadata,
    callback = callback,
    showResultScreen = true,
)
```

**Compare.** Identify the registered item with a Customer Item ID (exactly one registered item must
match) or an Entrupy ID. If both are supplied, the Entrupy ID takes precedence. If an item was
registered without a Customer Item ID, compare it by its Entrupy ID.

```kotlin
val compareByCustomerItemId = mapOf<String, Any?>(
    METADATA_KEY_CAPTURE_WORKFLOW to CAPTURE_WORKFLOW_FINGERPRINT_COMPARE,
    METADATA_KEY_CUSTOMER_ITEM_ID to "SKU-001",
)

val compareByEntrupyId = mapOf<String, Any?>(
    METADATA_KEY_CAPTURE_WORKFLOW to CAPTURE_WORKFLOW_FINGERPRINT_COMPARE,
    METADATA_KEY_ENTRUPY_ID to "ABC123",
)
```

Compare opens region capture once it resolves exactly one item. Otherwise it closes and calls
`onCaptureError` with one of these codes:

| Condition | Error code |
|-----------|------------|
| Neither identifier supplied | `EntrupyErrorCode.MISSING_METADATA_KEY` (2004) |
| No matching registered item | `EntrupyErrorCode.SEARCH_ITEM_NOT_FOUND` (1002) |
| Several items match the Customer Item ID | `EntrupyErrorCode.SEARCH_ITEM_FOUND` (1001) |
| Fingerprint workflow not enabled for the account | `EntrupyErrorCode.WORKFLOW_NOT_PERMITTED` (2005) |

#### 7. Customize SDK Colors (optional)

SDK screens don't inherit your app's theme. To match your app, implement `EntrupyTheme` and assign
it in `Application.onCreate`, after `EntrupyApp.init`. Each color is nullable and falls back to the
default Entrupy color on its own:

```kotlin
class MyAppEntrupyTheme : EntrupyTheme {
    override val backgroundColor: Int? = Color.WHITE
    override val foregroundColor: Int? = Color.BLACK
    override val borderColor: Int? = Color.LTGRAY
}

EntrupyApp.sharedInstance().theme = MyAppEntrupyTheme()
```

The sample's **SDK Theme** picker switches between the default styling and three example themes
(`theme/SampleSdkTheme.kt`). **Partial** sets only a white background, to show why a light
background also needs a foreground color. A new theme applies to SDK flows opened after the change.
See [UI Theme Customization](https://developer.entrupy.com/docs/mobile-sdks/android/sdk-reference/ui-theme-customization)
for what each color changes.

### ConfigMetadata Reference

The SDK uses a flexible metadata dictionary to configure the capture flow. Below are the metadata keys used by the v2 SDK.

#### Metadata Keys

| Key | Constant | Description | Example Values |
|-----|----------|-------------|----------------|
| `brand` | `METADATA_KEY_BRAND` | **Required** for authentication. Brand name in Title Case | `"Louis Vuitton"`, `"Nike"`, `"Bape"` |
| `item_type` | `METADATA_KEY_ITEM_TYPE` | Item type within a brand, in Title Case | `"Outerwear"`, `"Tops"`, `"Bottoms"`, `"Hats"` |
| `customer_item_id` | `METADATA_KEY_CUSTOMER_ITEM_ID` | **Required** for authentication. Your internal SKU/ID (max 256 characters) | `"SKU-12345"`, `"INV-2024-001"` |
| `entrupy_id` | `METADATA_KEY_ENTRUPY_ID` | Direct Fingerprint Compare lookup by Entrupy ID | `"ABC123"` |
| `capture_workflow` | `METADATA_KEY_CAPTURE_WORKFLOW` | Starts a fingerprint flow | `CAPTURE_WORKFLOW_FINGERPRINT_REGISTER`, `CAPTURE_WORKFLOW_FINGERPRINT_COMPARE` |
| `mode_id` | `METADATA_KEY_MODE_ID` | Operation mode | `"add"`, `"check"` |

> The capture flow matches a configuration using `brand` + `item_type` (case-insensitive). If only `brand` matches, the SDK opens the item type menu scoped to that brand. If the brand doesn't match, the SDK opens the full brand selection menu.

`ConfigMetadata` is a `Map<String, Any?>`, so build it with `mapOf` or `buildMap`:

#### Example

```kotlin
val metadata = mapOf<String, Any?>(
    METADATA_KEY_BRAND to "Bape",
    METADATA_KEY_ITEM_TYPE to "Outerwear",
    METADATA_KEY_CUSTOMER_ITEM_ID to "BAPE-001",
)
```

## Project Structure

```
app/src/main/java/com/entrupy/sample/
├── EntrupySampleApplication.kt  # SDK initialization
├── MainActivity.kt              # Main entry point
├── network/                     # Partner backend API
│   ├── PartnerApi.kt
│   └── PartnerApiClient.kt
├── theme/
│   └── SampleSdkTheme.kt        # SDK theme examples and picker storage
└── ui/
    ├── screens/
    │   └── MainScreen.kt        # SDK integration demo
    └── theme/

app/src/main/res/xml/
└── network_security_config.xml  # Trusts user CAs in debug builds (proxy inspection)
```

## License

Copyright © Entrupy Inc. All rights reserved.
