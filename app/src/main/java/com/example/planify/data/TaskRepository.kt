package com.example.planify.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.planify.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Extensión de DataStore asociada al Context (singleton por proceso)
private val Context.dataStore by preferencesDataStore(name = "tasks_prefs")

/**
 * Repositorio de tareas usando DataStore Preferences + kotlinx.serialization.
 * Persiste la lista de tareas como JSON sin necesidad de Room ni SQLite.
 */
class TaskRepository(private val context: Context) {

    private val tasksKey = stringPreferencesKey("tasks_list")

    /** Flow reactivo que emite la lista de tareas cada vez que cambia en DataStore. */
    val tasksFlow: Flow<List<Task>> = context.dataStore.data.map { preferences ->
        val tasksJson = preferences[tasksKey] ?: "[]"
        try {
            Json.decodeFromString<List<Task>>(tasksJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Persiste la lista completa de tareas en DataStore como JSON. */
    suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { preferences ->
            preferences[tasksKey] = Json.encodeToString(tasks)
        }
    }
}
