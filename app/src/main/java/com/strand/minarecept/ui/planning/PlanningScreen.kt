package com.strand.minarecept.ui.planning

import android.widget.CalendarView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material.icons.rounded.ViewWeek
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.strand.minarecept.R
import com.strand.minarecept.data.local.MealPlan
import com.strand.minarecept.ui.components.DatePill
import com.strand.minarecept.ui.components.MealPlanListCard
import com.strand.minarecept.ui.components.RecipeSpacer
import com.strand.minarecept.ui.components.horizontalSwipe
import com.strand.minarecept.util.capitalizeWords
import com.strand.minarecept.util.lerp3
import com.google.accompanist.pager.ExperimentalPagerApi
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

typealias CalendarWeek = List<LocalDate>


enum class SheetState { OPEN, CLOSED }

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun PlanningScreen(
    contentPadding: PaddingValues,
    openLunchSheet: () -> Unit,
    openDinnerSheet: () -> Unit,
    planningViewModel: PlanningViewModel = hiltViewModel()
) {
    val selectedDate by planningViewModel.selectedDate.collectAsState()

    val heightOfSheetContent = remember { mutableStateOf(0f) }

    Column(
        Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            /**
             * Setup swiping feature on the whole screen
             */
            val dragRange = constraints.maxHeight.toFloat()

            val sheetState = rememberSwipeableState(initialValue = SheetState.CLOSED)

            Box(
                modifier = Modifier
                    .swipeable(
                        state = sheetState,
                        anchors = mapOf(
                            0f to SheetState.CLOSED,
                            -dragRange to SheetState.OPEN
                        ),
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Vertical,
                        reverseDirection = true
                    ),
                contentAlignment = Alignment.TopStart
            ) {
                val openFraction = if (sheetState.offset.value.isNaN()) {
                    0f
                } else {
                    -sheetState.offset.value / dragRange
                }.coerceIn(0f, 1f)

                val opened = (sheetState.targetValue == SheetState.OPEN)
                // datePill=90.dp + paddingBottom=16.dp + paddingTop=4.dp -> Total=110.dp
                val closedHeight = with(LocalDensity.current) { 110.dp.toPx() }

                val offsetY = remember(closedHeight, heightOfSheetContent.value, openFraction) {
                    lerp3(
                        closedHeight-heightOfSheetContent.value,
                        0f,
                        openFraction
                    ) + heightOfSheetContent.value
                }

                /**
                 * The actual content starts here.
                 */
                Box(modifier = Modifier.fillMaxSize()) {
                    DatePickerSheet(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                heightOfSheetContent.value = coordinates.size.height.toFloat()
                            },
                        openFraction = openFraction,
                        heightOfSheetContent = heightOfSheetContent.value,
                        closedSheetHeight = closedHeight,
                        opened = opened,
                        selectedDate = selectedDate,
                        onDateSelected = {
                            planningViewModel.onDateSelectedChange(it)
                        }
                    )

                    AnimatedContent(
                        modifier = Modifier.graphicsLayer { translationY = offsetY },
                        targetState = selectedDate,
                        transitionSpec = {
                            if (targetState.isAfter(initialState)) {
                                slideInHorizontally { width -> width } with
                                        slideOutHorizontally { width -> -width }
                            } else {
                                slideInHorizontally { width -> -width }  with
                                        slideOutHorizontally { width -> width }
                            }.using(SizeTransform(clip = false))
                        }
                    ) { targetDate ->
                        PlanningScreenBody(
                            modifier = Modifier
                                .horizontalSwipe(
                                    onLeft = {
                                        planningViewModel
                                            .onDateSelectedChange(targetDate.minusDays(1L))
                                    },
                                    onRight = {
                                        planningViewModel
                                            .onDateSelectedChange(targetDate.plusDays(1L))
                                    }
                                ),
                            selectedDate = targetDate,
                            openLunchSheet = openLunchSheet,
                            openDinnerSheet = openDinnerSheet
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun PlanningScreenBody(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    openLunchSheet: () -> Unit,
    openDinnerSheet: () -> Unit,
    planningViewModel: PlanningViewModel = hiltViewModel()
) {
    val mealPlan by planningViewModel.loadMealPlanByDate(selectedDate).observeAsState()
    val isDayView by planningViewModel.isDayView.observeAsState(initial = true)

    Column(modifier = modifier) {
        RecipeSpacer()

        val formattedDate by remember(selectedDate) {
            mutableStateOf(
                selectedDate
                    .format(DateTimeFormatter.ofPattern("EEEE, d MMMM"))
                    .capitalizeWords()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = {
                    /*TODO: toggle between weekView and dayView*/
                    planningViewModel.onIsDayViewChange()
                }
            ) {
                if (isDayView)
                    Icon(
                        imageVector = Icons.Rounded.ViewWeek,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                else
                    Icon(
                        imageVector = Icons.Rounded.ViewDay,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
            }

        }


        MealPlanListCard(
            mealType = "Lunch",
            mealTitle = mealPlan?.lunch ?: "Lägg till ett recept till lunch",
            editOnClick = openLunchSheet,
            clearOnClick = {
                if (mealPlan?.lunch != null) {
                    planningViewModel.insertReplaceMealPlan(
                        MealPlan(
                            date = selectedDate,
                            lunch = null,
                            dinner = mealPlan?.dinner
                        )
                    )
                }
            }
        )

        MealPlanListCard(
            mealType = "Middag",
            mealTitle = mealPlan?.dinner ?: "Lägg till ett recept till middag",
            editOnClick = openDinnerSheet,
            clearOnClick = {
                if (mealPlan?.dinner != null) {
                    planningViewModel.insertReplaceMealPlan(
                        MealPlan(
                            date = selectedDate,
                            lunch = mealPlan?.lunch,
                            dinner = null
                        )
                    )
                }
            }
        )
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun DatePickerSheet(
    modifier: Modifier = Modifier,
    openFraction: Float,
    heightOfSheetContent: Float,
    closedSheetHeight: Float,
    opened: Boolean,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val offsetY = remember(closedSheetHeight, heightOfSheetContent, openFraction) {
        lerp3(closedSheetHeight-heightOfSheetContent, 0f, openFraction)
    }

    androidx.compose.material3.Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = offsetY
            }
            .heightIn(min = closedSheetHeight.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedContent(
                targetState = opened
            ) { isOpen ->
                if (isOpen) {
                    CustomCalendarView(
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected
                    )
                } else {
                    CustomWeekCalendarView(
                        selectedDate = selectedDate,
                        onDateSelected = onDateSelected
                    )
                }
            }
        }

    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun CustomWeekCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val weekFields = remember { WeekFields.of(Locale.getDefault()) }

    // Construct the list of all LocalDates of the current week
    val firstDayOfWeek by remember(selectedDate) {
        mutableStateOf(selectedDate.with(weekFields.dayOfWeek(), 1L))
    }
    val lastDayOfWeek by remember(selectedDate) {
        mutableStateOf(selectedDate.with(weekFields.dayOfWeek(), 7L))
    }
    var weekList by remember(firstDayOfWeek, lastDayOfWeek) {
        val initialList = mutableListOf<LocalDate>()
        var dat = firstDayOfWeek
        while (!dat.isAfter(lastDayOfWeek)) {
            initialList.add(dat)
            dat = dat.plusDays(1)
        }
        mutableStateOf(initialList)
    }

    val nextWeek by remember(weekList) {
        mutableStateOf(weekList.map { it.plusDays(7) } as MutableList<LocalDate>)
    }
    val prevWeek by remember(weekList) {
        mutableStateOf(weekList.map { it.minusDays(7) } as MutableList<LocalDate>)
    }

    // Swipeable week layout
    AnimatedContent(
        targetState = weekList,
        transitionSpec = {
            // Compare the incoming date with the target date to decide animation direction
            if (targetState.first().isAfter(initialState.first())) {
                slideInHorizontally { width -> width } with
                    slideOutHorizontally { width -> -width }
            } else {
                slideInHorizontally { width -> -width } with
                    slideOutHorizontally { width -> width }
            }.using(
                // Disable clipping since the faded slide-in/out should be displayed out of bounds.
                SizeTransform(clip = false)
            )
        }
    ) { targetWeek ->
        Week(
            modifier = Modifier.horizontalSwipe(
                onLeft = { weekList = prevWeek },
                onRight = { weekList = nextWeek }
            ),
            week = targetWeek,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    }

}


@Composable
private fun Week(
    modifier: Modifier = Modifier,
    week: CalendarWeek,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (day in week) {
            val textDayOfWeek = remember(day) {
                day.format(DateTimeFormatter.ofPattern("EEE")).capitalizeWords()
            }
            DatePill(
                localDateObject = day,
                textDayOfWeek = textDayOfWeek,
                numberDayOfWeek = day.dayOfMonth,
                selected = selectedDate == day,
                onSelected = { onDateSelected(day) }
            )
        }
    }
}


@Composable
private fun CustomCalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Get the current date in milliseconds - to initialize the calendar date
    val millis: Long = remember(selectedDate) {
        val ldt = selectedDate.atStartOfDay()
        val zdt = ldt.atZone(ZoneId.systemDefault())
        zdt.toInstant().toEpochMilli()
    }

    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(ContextThemeWrapper(context, R.style.CalenderViewCustom))
                .apply {
                    date = millis
                }
        },
        update = { view ->
            // This reacts to onClicks in the CalendarView
            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDate.of(year, month+1, dayOfMonth)
                )
            }
            // This updates the date when it changes from some external process
            view.date = millis
        }
    )
}
