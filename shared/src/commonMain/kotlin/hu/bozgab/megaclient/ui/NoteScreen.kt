package hu.bozgab.megaclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.model.Note
import hu.bozgab.megaclient.service.UserStorage
import hu.bozgab.megaclient.ui.model.NoteModel
import hu.bozgab.megaclient.util.AppColors
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import kotlin.time.Instant

private val dateTimeFormat = LocalDateTime.Format {
    year()
    char('.')
    monthNumber()
    char('.')
    day()
    char(' ')
    hour()
    char(':')
    minute()
}

private fun Instant.format(): String =
    dateTimeFormat.format(toLocalDateTime(TimeZone.currentSystemDefault()))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    model: NoteModel = koinInject(),
    userStorage: UserStorage = koinInject()
) {
    val currentUser = userStorage.user
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jegyzetek") },
                actions = {
                    IconButton(onClick = { model.create() }) {
                        Icon(Icons.Default.Add, contentDescription = "Új jegyzet")
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
        val interactionSource = remember { MutableInteractionSource() }
        PullToRefreshBox(
            isRefreshing = model.isLoading,
            onRefresh = { model.loadNotes() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { model.tryExitEditMode { model.cancelEdit() } }
        ) {
            if (model.isLoading && model.notes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (model.isCreatingNew) {
                        item {
                            NoteEditCard(
                                text = model.editingText,
                                color = model.editingColor,
                                onTextChange = { model.editingText = it },
                                onColorChange = { model.editingColor = it },
                                onApply = { model.save() },
                                onCancel = { model.tryExitEditMode { model.cancelEdit() } }
                            )
                        }
                    }
                    items(model.notes, key = { it.id }) { note ->
                        if (model.editingNoteId == note.id) {
                            NoteEditCard(
                                text = model.editingText,
                                color = model.editingColor,
                                onTextChange = { model.editingText = it },
                                onColorChange = { model.editingColor = it },
                                onApply = { model.save() },
                                onCancel = { model.tryExitEditMode { model.cancelEdit() } }
                            )
                        } else {
                            NoteViewCard(
                                note = note,
                                onClick = { model.edit(note) },
                                onDelete = { model.delete(note.id) }
                            )
                        }
                    }
                }
            }

            if (model.showUnsavedDialog) {
                AlertDialog(
                    onDismissRequest = { model.onDismissUnsavedDialog(false) },
                    title = { Text("Nem mentett módosítások") },
                    text = { Text("Elveti a módosításokat?") },
                    confirmButton = {
                        TextButton(onClick = { model.onDismissUnsavedDialog(true) }) {
                            Text("Elvetés")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { model.onDismissUnsavedDialog(false) }) {
                            Text("Mégse")
                        }
                    }
                )
            }

            model.error?.let {
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { model.loadNotes() }) { Text("Újra") }
                    }
                ) { Text(it) }
            }
        }
    }
}

@Composable
fun NoteViewCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = AppColors.getSecondary(note.color)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.note, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val updateTime = note.updatedAt ?: note.createdAt
                    val updatedBy = note.updatedBy ?: note.createdBy
                    Text(
                        text = "Frissítve: ${updateTime.format()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Szerkesztő: $updatedBy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menü")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Szín") },
                            onClick = {
                                showMenu = false
                                onClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Törlés") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteEditCard(
    text: String,
    color: String,
    onTextChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = true, onClick = {}),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.getSecondary(color)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Jegyzet tartalma...") },
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(AppColors.colors.keys.toList()) { colorName ->
                    val appColor = AppColors.colors[colorName]!!
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(appColor.primary)
                            .border(
                                width = if (color == colorName) 2.dp else 1.dp,
                                color = if (color == colorName) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { onColorChange(colorName) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Mégse")
                }
                IconButton(onClick = onApply) {
                    Icon(Icons.Default.Check, contentDescription = "Mentés")
                }
            }
        }
    }
}
