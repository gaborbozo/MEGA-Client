package hu.bozgab.megaclient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import hu.bozgab.megaclient.di.appModule
import hu.bozgab.megaclient.model.state.Screen
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.HomeScreen
import hu.bozgab.megaclient.ui.NoteScreen
import hu.bozgab.megaclient.ui.NotificationOverlay
import hu.bozgab.megaclient.ui.SettingsScreen
import hu.bozgab.megaclient.ui.ShoppingListScreen
import hu.bozgab.megaclient.util.AppColors
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        },
    ) {
        AppContent()
    }
}

@Composable
fun AppContent(userStorage: UserStorage = koinInject()) {
    MaterialTheme {
        val currentUser = userStorage.user
        var selectedScreen by remember { mutableStateOf(Screen.Home) }

        // If user logs out, we should probably reset the selected screen to Home or Settings
        // But the logic below already handles restricting access.

        val currentEffectiveScreen = if (currentUser == null) Screen.Settings else selectedScreen

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = if (currentUser != null) AppColors.getPrimary(currentUser.theme) else NavigationBarDefaults.containerColor
                ) {
                    Screen.entries.forEach { screen ->
                        val isSettings = screen == Screen.Settings
                        val isUserLoggedIn = currentUser != null

                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentEffectiveScreen == screen,
                            onClick = { selectedScreen = screen },
                            enabled = isUserLoggedIn || isSettings,
                            colors = if (currentUser != null) {
                                NavigationBarItemDefaults.colors(
                                    indicatorColor = AppColors.getSecondary(currentUser.theme),
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = Color.Black,
                                    unselectedIconColor = Color.Black.copy(alpha = 0.6f),
                                    unselectedTextColor = Color.Black.copy(alpha = 0.6f)
                                )
                            } else NavigationBarItemDefaults.colors()
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                if (currentUser == null) {
                    SettingsScreen()
                } else {
                    when (selectedScreen) {
                        Screen.Home -> HomeScreen()
                        Screen.ShoppingList -> ShoppingListScreen()
                        Screen.Note -> NoteScreen()
                        Screen.Settings -> SettingsScreen()
                    }
                }
                NotificationOverlay()
            }
        }
    }
}
