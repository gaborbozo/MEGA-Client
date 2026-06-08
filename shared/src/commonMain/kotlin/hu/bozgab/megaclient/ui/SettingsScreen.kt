package hu.bozgab.megaclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.SettingsModel
import hu.bozgab.megaclient.util.AppColors
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    model: SettingsModel = koinInject(),
    userStorage: UserStorage = koinInject()
) {
    val currentUser = userStorage.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (currentUser != null) AppColors.getPrimary(currentUser.theme) else Color.Transparent)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentUser == null) {
            Text("Bejelentkezés", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = model.username,
                onValueChange = { model.username = it },
                label = { Text("Felhasználónév") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = model.password,
                onValueChange = { model.password = it },
                label = { Text("Jelszó") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (model.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { model.login() },
                    enabled = model.canLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.getPrimary("grey"),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Bejelentkezés")
                }
            }

            model.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        } else {
            Text(
                "Bejelentkezve mint: ${currentUser.name}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text("Alapértelmezett téma (szín)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(AppColors.colors.keys.toList()) { colorName ->
                    val appColor = AppColors.colors[colorName]!!
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(appColor.primary)
                            .border(
                                width = if (currentUser.theme == colorName) 3.dp else 1.dp,
                                color = if (currentUser.theme == colorName) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { model.updateTheme(colorName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { model.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.getSecondary(currentUser.theme),
                    contentColor = Color.Black
                )
            ) {
                Text("Kijelentkezés")
            }
        }
    }
}
