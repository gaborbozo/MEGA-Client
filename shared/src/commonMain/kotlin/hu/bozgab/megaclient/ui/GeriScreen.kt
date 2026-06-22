package hu.bozgab.megaclient.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.GeriModel
import hu.bozgab.megaclient.util.AppColors
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeriScreen(
    model: GeriModel = koinInject(),
    userStorage: UserStorage = koinInject()
) {
    val currentUser = userStorage.user
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Geri") },
                actions = {
                    IconButton(onClick = { model.uploadImage() }) {
                        Icon(Icons.Default.Add, contentDescription = "Új kép")
                    }
                    if (model.currentImageId != null) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Opciók")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Kép törlése") },
                                    onClick = {
                                        showMenu = false
                                        model.deleteCurrentImage()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = if (currentUser != null) {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.getPrimary(currentUser.theme),
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color.Black
                    )
                } else TopAppBarDefaults.topAppBarColors()
            )
        },
        containerColor = if (currentUser != null) AppColors.getPrimary(currentUser.theme) else MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = model.isLoading,
            onRefresh = { model.loadRandomImage() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (model.isLoading && model.currentImageBytes == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        model.currentImageBytes?.let { bytes ->
                            val bitmap = remember(bytes) {
                                try {
                                    bytes.decodeToImageBitmap()
                                } catch (_: Exception) {
                                    null
                                }
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Random Geri kép",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text("Hibás képformátum", color = Color.Red)
                            }
                        } ?: run {
                            if (!model.isLoading) {
                                Text("Nincs betöltött kép")
                            }
                        }
                    }
                }
            }
        }
    }
}
