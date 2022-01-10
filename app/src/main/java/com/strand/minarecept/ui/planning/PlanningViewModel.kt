package com.strand.minarecept.ui.planning

import androidx.lifecycle.*
import com.strand.minarecept.data.local.MealPlan
import com.strand.minarecept.data.local.room.PlanningDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PlanningViewModel @Inject constructor(
    private val planningDao: PlanningDao
) : ViewModel() {

    private val _mealPlanText = MutableLiveData("")
    val mealPlanText: LiveData<String> = _mealPlanText

    fun onMealPlanTextChange(newMealPlanText: String) {
        _mealPlanText.value = newMealPlanText
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate>
        get() = _selectedDate.asStateFlow()

    fun onDateSelectedChange(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    private val _isDayView = MutableLiveData(true)
    val isDayView: LiveData<Boolean> = _isDayView

    fun onIsDayViewChange() {
        _isDayView.value = !_isDayView.value!!
    }

    fun loadMealPlanByDate(date: LocalDate) = planningDao.getMealPlanByDate(date).asLiveData()

    fun insertReplaceMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            planningDao.insertReplaceMealPlan(mealPlan)
        }
        onMealPlanTextChange("")
    }

    fun updateMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        planningDao.updateMealPlan(mealPlan)
    }

    fun deleteMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        planningDao.deleteMealPlan(mealPlan)
    }

}