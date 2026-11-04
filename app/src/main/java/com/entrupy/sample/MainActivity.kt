package com.entrupy.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.entrupy.sample.ui.screens.MainScreen
import com.entrupy.sample.ui.screens.ResultsScreen
import com.entrupy.sample.ui.theme.AccentGold
import com.entrupy.sample.ui.theme.CardBackground
import com.entrupy.sample.ui.theme.TextSecondary
import com.entrupy.sample.ui.theme.EntrupySampleTheme

private enum class SampleTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Results("Results", Icons.AutoMirrored.Filled.List),
}

/**
 * Main Activity for the Entrupy Sample App.
 *
 * Two tabs, like the iOS sample: Home logs in and starts a capture, Results lists the past
 * authentications with `EntrupyApp.searchAuthenticationItems`.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EntrupySampleTheme {
                var selectedTab by rememberSaveable { mutableStateOf(SampleTab.Home) }
                // Keeps what the Home form holds while the Results tab is open.
                val tabStates = rememberSaveableStateHolder()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(containerColor = CardBackground) {
                            SampleTab.entries.forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Icon(tab.icon, contentDescription = null) },
                                    label = { Text(tab.label) },
                                    colors =
                                        NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            selectedTextColor = AccentGold,
                                            indicatorColor = AccentGold,
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary,
                                        ),
                                )
                            }
                        }
                    },
                ) { innerPadding ->
                    tabStates.SaveableStateProvider(selectedTab) {
                        when (selectedTab) {
                            SampleTab.Home -> MainScreen(modifier = Modifier.padding(innerPadding))
                            SampleTab.Results ->
                                ResultsScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}
