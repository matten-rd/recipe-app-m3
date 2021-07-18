package com.example.minarecept.data.local.room

import androidx.room.*
import com.example.minarecept.data.local.Recipe
import com.example.minarecept.data.local.sortOrderOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {


    fun getPagingSource(
        query: String,
        sortOrder: Int,
        onlyFavorite: Boolean,
        durationStart: Long,
        durationEnd: Long,
    ): Flow<List<Recipe>> =
        when (sortOrder) {
            sortOrderOptions.optionsStringResList[0] -> pagingSourceSortedByName(
                searchQuery = query,
                onlyFavorite = onlyFavorite,
                sortOrder = 0,
                durationStart = durationStart,
                durationEnd = durationEnd
            )
            sortOrderOptions.optionsStringResList[1] -> pagingSourceSortedByName(
                searchQuery = query,
                onlyFavorite = onlyFavorite,
                sortOrder = 1,
                durationStart = durationStart,
                durationEnd = durationEnd
            )
            sortOrderOptions.optionsStringResList[2] -> pagingSourceSortedByName(
                searchQuery = query,
                onlyFavorite = onlyFavorite,
                sortOrder = 2,
                durationStart = durationStart,
                durationEnd = durationEnd
            )
            sortOrderOptions.optionsStringResList[3] -> pagingSourceSortedByName(
                searchQuery = query,
                onlyFavorite = onlyFavorite,
                sortOrder = 3,
                durationStart = durationStart,
                durationEnd = durationEnd
            )

            else -> pagingSourceSortedByName(
                searchQuery = query,
                onlyFavorite = onlyFavorite,
                sortOrder = 0,
                durationStart = durationStart,
                durationEnd = durationEnd
            )
        }

    @Query("""
            SELECT * FROM recipes_table
            WHERE (is_favorite = :onlyFavorite OR is_favorite = 1)
                AND title LIKE '%' || :searchQuery || '%'
                AND (total_time BETWEEN :durationStart AND :durationEnd OR NULLIF(total_time, 0) IS NULL)
            ORDER BY
                CASE :sortOrder WHEN 0 THEN title END COLLATE NOCASE ASC,
                CASE :sortOrder WHEN 1 THEN title END COLLATE NOCASE DESC,
                CASE :sortOrder WHEN 2 THEN is_favorite END DESC,
                CASE :sortOrder WHEN 3 THEN clicked_count END DESC
            """
    )
    fun pagingSourceSortedByName(
        searchQuery: String,
        onlyFavorite: Boolean,
        sortOrder: Int,
        durationStart: Long,
        durationEnd: Long
    ): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes_table WHERE recipe_id = :id")
    fun loadRecipeById(id: Int): Flow<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}