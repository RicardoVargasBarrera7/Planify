package com.example.planify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.planify.data.TaskRepository
import com.example.planify.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel para la gestión de tareas.
 * Expone el estado reactivo de la lista de tareas y las operaciones CRUD.
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // ── Lista de tareas (StateFlow reactivo) ──────────────────────────────
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // ── Estado de carga inicial ────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Carga las tareas persistidas desde DataStore al iniciar
        viewModelScope.launch {
            repository.tasksFlow.collect { savedTasks ->
                _tasks.value = savedTasks
                _isLoading.value = false
            }
        }
    }

    // ── Agregar tarea ──────────────────────────────────────────────────────
    fun addTask(title: String, description: String, date: String) {
        if (title.isBlank()) return
        val newTask = Task(
            id          = UUID.randomUUID().toString(),
            title       = title.trim(),
            description = description.trim(),
            date        = date.trim(),
            isCompleted = false
        )
        val updated = _tasks.value + newTask
        saveAndUpdate(updated)
    }

    // ── Editar tarea existente ─────────────────────────────────────────────
    fun updateTask(updatedTask: Task) {
        val updated = _tasks.value.map { task ->
            if (task.id == updatedTask.id) updatedTask else task
        }
        saveAndUpdate(updated)
    }

    // ── Eliminar tarea ─────────────────────────────────────────────────────
    fun deleteTask(taskId: String) {
        val updated = _tasks.value.filter { it.id != taskId }
        saveAndUpdate(updated)
    }

    // ── Alternar estado completado/pendiente ───────────────────────────────
    fun toggleTaskCompletion(taskId: String) {
        val updated = _tasks.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
        saveAndUpdate(updated)
    }

    // ── Persiste la lista actualizada en DataStore ─────────────────────────
    private fun saveAndUpdate(tasks: List<Task>) {
        _tasks.value = tasks
        viewModelScope.launch {
            repository.saveTasks(tasks)
        }
    }
}

/**
 * Factory para instanciar TaskViewModel con su dependencia (TaskRepository).
 * Necesario porque el ViewModel tiene un constructor con parámetros.
 */
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
