package com.example.planify.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.planify.model.Task
import com.example.planify.viewmodel.TaskViewModel

/**
 * Pantalla de gestión de tareas con CRUD completo.
 * Usa LazyColumn para listar tareas y diálogos para agregar/editar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel : TaskViewModel,
    onBack    : () -> Unit
) {
    val tasks     by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ── Estado de diálogos ─────────────────────────────────────────────────
    var showAddDialog  by remember { mutableStateOf(false) }
    var taskToEdit     by remember { mutableStateOf<Task?>(null) }
    var taskToDelete   by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Mis Tareas",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text  = "${tasks.count { it.isCompleted }}/${tasks.size} completadas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon    = { Icon(Icons.Default.Add, contentDescription = "Agregar tarea") },
                text    = { Text("Nueva tarea") }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ── Cargando ───────────────────────────────────────────────
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // ── Lista vacía ────────────────────────────────────────────
                tasks.isEmpty() -> {
                    Column(
                        modifier            = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📝", style = MaterialTheme.typography.displayMedium)
                        Text(
                            text  = "No tienes tareas aún",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text  = "Toca el botón + para agregar una",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // ── Lista de tareas ────────────────────────────────────────
                else -> {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize(),
                        contentPadding      = PaddingValues(
                            start  = 16.dp,
                            end    = 16.dp,
                            top    = 12.dp,
                            bottom = 88.dp   // espacio para el FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tareas pendientes primero
                        val pending   = tasks.filter { !it.isCompleted }
                        val completed = tasks.filter { it.isCompleted }

                        if (pending.isNotEmpty()) {
                            item {
                                SectionHeader(title = "Pendientes (${pending.size})")
                            }
                            items(pending, key = { it.id }) { task ->
                                TaskItem(
                                    task     = task,
                                    onToggle = { viewModel.toggleTaskCompletion(task.id) },
                                    onEdit   = { taskToEdit = task },
                                    onDelete = { taskToDelete = task }
                                )
                            }
                        }

                        if (completed.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                SectionHeader(title = "Completadas (${completed.size})")
                            }
                            items(completed, key = { it.id }) { task ->
                                TaskItem(
                                    task     = task,
                                    onToggle = { viewModel.toggleTaskCompletion(task.id) },
                                    onEdit   = { taskToEdit = task },
                                    onDelete = { taskToDelete = task }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Diálogo: Agregar tarea ─────────────────────────────────────────────
    if (showAddDialog) {
        TaskDialog(
            title         = "Nueva tarea",
            confirmLabel  = "Agregar",
            onDismiss     = { showAddDialog = false },
            onConfirm     = { t, d, date ->
                viewModel.addTask(t, d, date)
                showAddDialog = false
            }
        )
    }

    // ── Diálogo: Editar tarea ──────────────────────────────────────────────
    taskToEdit?.let { task ->
        TaskDialog(
            title         = "Editar tarea",
            confirmLabel  = "Guardar",
            initialTitle  = task.title,
            initialDesc   = task.description,
            initialDate   = task.date,
            onDismiss     = { taskToEdit = null },
            onConfirm     = { t, d, date ->
                viewModel.updateTask(task.copy(title = t, description = d, date = date))
                taskToEdit = null
            }
        )
    }

    // ── Diálogo: Confirmar eliminación ─────────────────────────────────────
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            icon             = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar tarea") },
            text  = {
                Text("¿Deseas eliminar \"${task.title}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(task.id)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

// ── Encabezado de sección ──────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.labelLarge,
        color      = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier   = Modifier.padding(vertical = 4.dp)
    )
}

// ── Tarjeta de tarea individual ────────────────────────────────────────────
@Composable
private fun TaskItem(
    task    : Task,
    onToggle: () -> Unit,
    onEdit  : () -> Unit,
    onDelete: () -> Unit
) {
    val cardColor by animateColorAsState(
        targetValue = if (task.isCompleted)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "cardColor"
    )

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Botón de completar ─────────────────────────────────────────
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (task.isCompleted)
                        Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (task.isCompleted) "Marcar pendiente" else "Marcar completada",
                    tint = if (task.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }

            // ── Contenido de la tarea ──────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text           = task.title,
                    style          = MaterialTheme.typography.titleSmall,
                    fontWeight     = FontWeight.SemiBold,
                    color          = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines       = 1,
                    overflow       = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text     = task.description,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (task.date.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.primary,
                            modifier           = Modifier.size(12.dp)
                        )
                        Text(
                            text  = task.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // ── Acciones: Editar / Eliminar ────────────────────────────────
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector        = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint               = MaterialTheme.colorScheme.secondary,
                        modifier           = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector        = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint               = MaterialTheme.colorScheme.error,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Diálogo reutilizable para agregar / editar tarea ──────────────────────
@Composable
private fun TaskDialog(
    title        : String,
    confirmLabel : String,
    initialTitle : String = "",
    initialDesc  : String = "",
    initialDate  : String = "",
    onDismiss    : () -> Unit,
    onConfirm    : (title: String, description: String, date: String) -> Unit
) {
    var taskTitle by remember { mutableStateOf(initialTitle) }
    var taskDesc  by remember { mutableStateOf(initialDesc) }
    var taskDate  by remember { mutableStateOf(initialDate) }
    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Título ─────────────────────────────────────────────────
                OutlinedTextField(
                    value         = taskTitle,
                    onValueChange = {
                        taskTitle  = it
                        titleError = false
                    },
                    label         = { Text("Título *") },
                    singleLine    = true,
                    isError       = titleError,
                    supportingText = if (titleError) {
                        { Text("El título es obligatorio") }
                    } else null,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // ── Descripción ────────────────────────────────────────────
                OutlinedTextField(
                    value         = taskDesc,
                    onValueChange = { taskDesc = it },
                    label         = { Text("Descripción") },
                    maxLines      = 3,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // ── Fecha ──────────────────────────────────────────────────
                OutlinedTextField(
                    value         = taskDate,
                    onValueChange = { taskDate = it },
                    label         = { Text("Fecha (ej. 02/06/2026)") },
                    singleLine    = true,
                    leadingIcon   = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isBlank()) {
                        titleError = true
                    } else {
                        onConfirm(taskTitle.trim(), taskDesc.trim(), taskDate.trim())
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
