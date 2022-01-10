package com.strand.minarecept.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.strand.minarecept.R
import com.strand.minarecept.ui.components.PossibleAnswer
import com.strand.minarecept.util.categories
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preference")

val sortOrderOptions = PossibleAnswer.SingleChoice(
    optionsStringResList = listOf(
        R.string.by_name,
        R.string.favorites_first,
        R.string.by_popularity,
        R.string.by_latest_published
    )
)

val categoriesOptions = PossibleAnswer.MultipleChoice(
    optionsStringList = categories
)

data class FilterPreferences(
    val sortOrder: Int = sortOrderOptions.optionsStringResList[0],
    val onlyFavorite: Boolean = false,
    val durationStart: Long = 0L,
    val durationEnd: Long = (100L*60*1000), // 100 min * 60 s * convert to milliseconds
    val categories: Set<String> = categoriesOptions.optionsStringList.toSet()
)

class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
            } else {
                Log.e(TAG, "Unknown error occurred: ", exception)
            }
            emit(emptyPreferences())
        }
        .map { preferences ->
            val sortOrder = preferences[SORT_ORDER] ?: sortOrderOptions.optionsStringResList[0]
            val onlyFavorite = preferences[ONLY_FAVORITE] ?: false
            val durationStart = preferences[DURATION_START] ?: 0L
            val durationEnd = preferences[DURATION_END] ?: (70L*60*1000)
            val categories = preferences[CATEGORIES] ?: categoriesOptions.optionsStringList.toSet()
            FilterPreferences(
                sortOrder = sortOrder,
                onlyFavorite = onlyFavorite,
                durationStart = durationStart,
                durationEnd = durationEnd,
                categories = categories
            )
        }

    suspend fun updateSortOrder(sortOrder: Int) {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER] = sortOrder
        }
    }

    suspend fun updateOnlyFavorite(onlyFavorite: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONLY_FAVORITE] = onlyFavorite
        }
    }

    suspend fun updateDuration(durationStart: Long, durationEnd: Long) {
        dataStore.edit { preferences ->
            preferences[DURATION_START] = durationStart
            preferences[DURATION_END] = durationEnd
        }
    }

    suspend fun updateCategories(categories: Set<String>) {
        dataStore.edit { preferences ->
            preferences[CATEGORIES] = categories
        }
    }


    companion object PreferencesKeys {
        val SORT_ORDER = intPreferencesKey("sort_order")
        val ONLY_FAVORITE = booleanPreferencesKey("only_favorite")
        val DURATION_START = longPreferencesKey("duration_start")
        val DURATION_END = longPreferencesKey("duration_end")
        val CATEGORIES = stringSetPreferencesKey("categories")
    }

}


private const val TAG = "PreferencesManager"