package com.strand.minarecept.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.strand.minarecept.data.local.MealPlan
import com.strand.minarecept.data.local.Recipe


@Database(entities = [Recipe::class, MealPlan::class], version = 7, exportSchema = false)
@TypeConverters(DateTimeTypeConverters::class, ListTypeConverter::class, SetTypeConverter::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun planningDao(): PlanningDao

}