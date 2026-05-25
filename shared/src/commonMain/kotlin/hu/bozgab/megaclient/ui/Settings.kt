package hu.bozgab.megaclient.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.SettingsModel
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
                    modifier = Modifier.fillMaxWidth()
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
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { model.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kijelentkezés")
            }
        }
    }
}
