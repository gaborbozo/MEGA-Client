package hu.bozgab.megaclient.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bozgab.megaclient.model.NoteDTO
import hu.bozgab.megaclient.ui.model.NoteModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(model: NoteModel = koinInject()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jegyzetek") },
                actions = {
                    IconButton(onClick = { model.create() }) {
                        Icon(Icons.Default.Add, contentDescription = "Új jegyzet")
                    }
                }
            )
        }
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
                                onTextChange = { model.editingText = it },
                                onApply = { model.save() },
                                onCancel = { model.tryExitEditMode { model.cancelEdit() } }
                            )
                        }
                    }
                    items(model.notes, key = { it.id }) { note ->
                        if (model.editingNoteId == note.id) {
                            NoteEditCard(
                                text = model.editingText,
                                onTextChange = { model.editingText = it },
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
    note: NoteDTO,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.note, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menü")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
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
    onTextChange: (String) -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = true, onClick = {}),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
