package hu.bozgab.megaclient.model.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val title: String, val icon: ImageVector) {
    Home("Kezdőlap", Icons.Default.Home),
    ShoppingList("Bevásárlólista", Icons.AutoMirrored.Filled.List),
    Settings("Beállítások", Icons.Default.Settings)
}
