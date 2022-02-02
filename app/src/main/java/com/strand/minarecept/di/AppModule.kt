package com.strand.minarecept.di

import android.app.Application
import androidx.room.Room
import com.strand.minarecept.data.local.room.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, RecipeDatabase::class.java, "recipe_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideRecipeDao(db: RecipeDatabase) = db.recipeDao()

    @Provides
    fun providePlanningDao(db: RecipeDatabase) = db.planningDao()

}