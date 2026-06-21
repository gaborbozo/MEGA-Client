package hu.bozgab.megaclient.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.model.ShoppingItem
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.ShoppingListModel
import hu.bozgab.megaclient.util.AppColors
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShoppingListScreen(
    model: ShoppingListModel = koinInject(),
    userStorage: UserStorage = koinInject()
) {
    val currentUser = userStorage.user
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bevásárlólista") },
                actions = {
                    if (model.yearWeek.isCurrentYearWeek()) {
                        IconButton(onClick = { model.create() }) {
                            Icon(Icons.Default.Add, contentDescription = "Hozzáadás")
                        }
                    } else {
                        IconButton(onClick = { model.jumpToCurrentWeek() }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Ugrás a jelenlegi hétre"
                            )
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
            onRefresh = { model.loadItems() },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { model.previousWeek() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Előző hét"
                        )
                    }
                    Text(
                        "${model.yearWeek.year}. ${model.yearWeek.week}. hét",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { model.nextWeek() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Következő hét"
                        )
                    }
                }

                if (model.isLoading && model.items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        model.items.forEach { item ->
                            ShoppingItemTile(
                                item = item,
                                onDelete = { model.confirmDelete(item.id) }
                            )
                        }
                    }
                }
            }
        }

        if (model.isCreatingNew) {
            AlertDialog(
                onDismissRequest = { model.cancelCreate() },
                title = { Text("Új tétel") },
                text = {
                    TextField(
                        value = model.newProduct,
                        onValueChange = { model.newProduct = it },
                        placeholder = { Text("Termék neve") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = { model.save() }) {
                        Text("Hozzáadás")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { model.cancelCreate() }) {
                        Text("Mégse")
                    }
                }
            )
        }

        if (model.itemToDeleteId != null) {
            AlertDialog(
                onDismissRequest = { model.cancelDelete() },
                title = { Text("Törlés megerősítése") },
                text = { Text("Biztosan törölni szeretnéd ezt a tételt?") },
                confirmButton = {
                    TextButton(
                        onClick = { model.delete() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Törlés")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { model.cancelDelete() }) {
                        Text("Mégse")
                    }
                }
            )
        }
    }
}

@Composable
fun ShoppingItemTile(
    item: ShoppingItem,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.product,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Törlés",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
