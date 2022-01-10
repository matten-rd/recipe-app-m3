package com.strand.minarecept.data.local.room

import androidx.room.*
import com.strand.minarecept.data.local.MealPlan
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PlanningDao {

    @Query("SELECT * FROM planning_table WHERE date = :targetDate")
    fun getMealPlanByDate(targetDate: LocalDate): Flow<MealPlan>

    @Query("SELECT date FROM planning_table WHERE date = :targetDate")
    fun checkDateExists(targetDate: LocalDate): LocalDate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplaceMealPlan(mealPlan: MealPlan)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMealPlan(mealPlan: MealPlan)

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)
}