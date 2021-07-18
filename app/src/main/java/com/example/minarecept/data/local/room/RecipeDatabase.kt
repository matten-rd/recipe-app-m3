package com.example.minarecept.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.minarecept.data.local.Recipe


@Database(entities = [Recipe::class], version = 3, exportSchema = false)
@TypeConverters(DateTimeTypeConverters::class, ListTypeConverter::class, SetTypeConverter::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

}