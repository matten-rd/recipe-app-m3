package com.strand.minarecept.data.local

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.strand.minarecept.ui.components.autocomplete.AutoCompleteEntity
import com.strand.minarecept.util.NoArg
import java.time.Duration
import java.time.OffsetDateTime

@Entity(tableName = "recipes_table")
@Immutable
data class Recipe(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "recipe_id") val recipeId: Int = 0,
    @ColumnInfo(name = "recipe_state") val recipeState: RecipeState,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "thumbnail_image") val thumbnailImage: String,
    @ColumnInfo(name = "recipe_url") val recipeUrl: String? = null,
    @ColumnInfo(name = "recipe_image") val recipeImage: String? = null,
    @ColumnInfo(name = "ingredients") val ingredients: List<String>? = null,
    @ColumnInfo(name = "instructions") val instructions: List<String>? = null,
    @ColumnInfo(name = "category") val category: List<String>? = null,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "yield") val yield: Int? = null,
    @ColumnInfo(name = "total_time") val totalTime: Duration? = null,
    @ColumnInfo(name = "published") val published: OffsetDateTime,
    @ColumnInfo(name = "last_updated") val lastUpdated: OffsetDateTime,
    @ColumnInfo(name = "clicked_count") val clickedCount: Int = 0
)

enum class RecipeState {
    JSONLD,
    TRAVERSE,
    WEBVIEW,
    IMAGE
}

@NoArg
data class FirebaseRecipe(
    val recipeId: String,
    val recipeState: RecipeState,
    val title: String,
    val description: String? = null,
    val thumbnailImage: String,
    val recipeUrl: String? = null,
    val recipeImage: String? = null,
    val ingredients: List<String>? = null,
    val instructions: List<String>? = null,
    val category: List<String>? = null,
    @get: PropertyName("favorite") @set: PropertyName("favorite") var isFavorite: Boolean = false,
    val yield: Int? = null,
    val totalTime: String? = null,
    val published: String,
    val lastUpdated: String
) : AutoCompleteEntity {
    override fun filter(query: String): Boolean {
        return title.contains(query, ignoreCase = true)
    }

}
