package com.strand.minarecept.data.local

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "planning_table")
@Immutable
data class MealPlan(
    @PrimaryKey @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "breakfast") val breakfast: String? = null,
    @ColumnInfo(name = "lunch") val lunch: String? = null,
    @ColumnInfo(name = "dinner") val dinner: String? = null
)