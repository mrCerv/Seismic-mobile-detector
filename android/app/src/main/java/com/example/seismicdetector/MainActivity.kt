package com.example.seismicdetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seismicdetector.data.PreferencesManager
import com.example.seismicdetector.ui.disclaimer.DisclaimerScreen
import com.example.seismicdetector.ui.history.HistoryScreen
import com.example.seismicdetector.ui.home.HomeScreen
import com.example.seismicdetector.ui.map.MapScreen
import com.example.seismicdetector.ui.settings.SettingsScreen
import com.example.seismicdetector.ui.theme.SeismicDetectorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Map : Screen("map", "Mappa", Icons.Default.Place)
    object History : Screen("history", "Storico", Icons.Default.History)
    object Settings : Screen("settings", "Impostazioni", Icons.Default.Settings)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeismicDetectorTheme {
                val disclaimerAccepted = remember { mutableStateOf(preferencesManager.hasAcceptedDisclaimer()) }

                if (!disclaimerAccepted.value) {
                    DisclaimerScreen(
                        onAcceptWithSharing = {
                            preferencesManager.setDisclaimerAccepted()
                            val settings = preferencesManager.settings.value
                            preferencesManager.updateSettings(settings.copy(shareDataConsent = true))
                            disclaimerAccepted.value = true
                        },
                        onAcceptLocalOnly = {
                            preferencesManager.setDisclaimerAccepted()
                            disclaimerAccepted.value = true
                        }
                    )
                } else {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Map, Screen.History, Screen.Settings)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.History.route) { HistoryScreen(onBack = { navController.popBackStack() }) }
            composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
