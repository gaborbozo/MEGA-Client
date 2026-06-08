package hu.bozgab.megaclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.util.AppColors
import org.koin.compose.koinInject

@Composable
fun HomeScreen(userStorage: UserStorage = koinInject()) {
    val currentUser = userStorage.user
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (currentUser != null) AppColors.getPrimary(currentUser.theme) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text("Kezdőlap", style = MaterialTheme.typography.headlineMedium)
    }
}
