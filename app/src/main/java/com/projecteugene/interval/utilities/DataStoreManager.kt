package com.projecteugene.interval.utilities

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val name = "${context.packageName}_preferences"
    private val prefRepeatable = booleanPreferencesKey("$name.repeatable")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = name)

    fun isRepeatable(): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[prefRepeatable] ?: false
            }
    }

    suspend fun toggleRepeatable() {
        context.dataStore.edit { settings ->
            val oldValue = settings[prefRepeatable]
            settings[prefRepeatable] = oldValue?.not() ?: true
        }
    }
}