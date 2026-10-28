package com.entrupy.sample.theme

import android.content.Context
import android.graphics.Color
import androidx.core.content.edit
import com.entrupy.sdk.app.EntrupyApp
import com.entrupy.sdk.model.EntrupyTheme

/** A light theme: white backgrounds, black text, and light gray borders. */
class SampleLightTheme : EntrupyTheme {
    override val backgroundColor: Int? = Color.WHITE
    override val foregroundColor: Int? = Color.BLACK
    override val borderColor: Int? = Color.LTGRAY
}

/**
 * Sets only the background. Text keeps its white default, so it is hard to read on white: a partner
 * who sets a light background also needs a foreground color.
 */
class SamplePartialTheme : EntrupyTheme {
    override val backgroundColor: Int? = Color.WHITE
    override val foregroundColor: Int? = null
    override val borderColor: Int? = null
}

/** A black theme: black backgrounds, white text, and dark gray borders. */
class SampleBlackTheme : EntrupyTheme {
    override val backgroundColor: Int? = Color.BLACK
    override val foregroundColor: Int? = Color.WHITE
    override val borderColor: Int? = Color.DKGRAY
}

/** The SDK themes this sample can switch between, stored so the choice survives a relaunch. */
enum class SampleThemeOption(val title: String) {
    SdkDefault("Default"),
    Light("Light"),
    Black("Black"),
    Partial("Partial");

    val theme: EntrupyTheme?
        get() =
            when (this) {
                SdkDefault -> null
                Light -> SampleLightTheme()
                Partial -> SamplePartialTheme()
                Black -> SampleBlackTheme()
            }

    /** Stores this option and hands its theme to the SDK for the next SDK flow. */
    fun apply(context: Context) {
        prefs(context).edit { putString(STORAGE_KEY, name) }
        EntrupyApp.sharedInstance().theme = theme
    }

    companion object {
        private const val PREFERENCES_NAME = "sample_app"
        private const val STORAGE_KEY = "sample_app_sdk_theme"

        fun stored(context: Context): SampleThemeOption =
            prefs(context).getString(STORAGE_KEY, null)?.let { stored ->
                entries.firstOrNull { it.name == stored }
            } ?: SdkDefault

        private fun prefs(context: Context) =
            context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
}
