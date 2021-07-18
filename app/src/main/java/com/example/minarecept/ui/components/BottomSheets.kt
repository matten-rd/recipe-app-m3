package com.example.minarecept.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asLiveData
import com.example.minarecept.data.local.sortOrderOptions
import com.example.minarecept.ui.home.HomeViewModel

enum class BottomSheetMainScreens { ADD, FILTER }

@Composable
fun AddNewRecipeBottomSheet(
    navigateToNewUrlRecipe: () -> Unit,
    navigateToNewImageRecipe: () -> Unit,
    navigateToPhotoActivity: () -> Unit
) {
    Column(
        modifier = Modifier.padding(bottom = 56.dp, top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(text = "Lägg till nytt recept", style = MaterialTheme.typography.h2)
        RecipeSpacer()

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToNewUrlRecipe() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AddLink,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            RecipeSpacer()
            Text(text = "Spara receptlänk", style = MaterialTheme.typography.h6)
        }
        RecipeSpacer()
        RecipeSpacer()

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToNewImageRecipe() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AddPhotoAlternate,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            RecipeSpacer()
            Text(text = "Spara en receptbild", style = MaterialTheme.typography.h6)
        }
        RecipeSpacer()
        RecipeSpacer()

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToPhotoActivity() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            RecipeSpacer()
            Text(text = "Ta bild på nytt recept", style = MaterialTheme.typography.h6)
        }
        RecipeSpacer()

    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun FilterBottomSheet(
    onSaveClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val preferences by homeViewModel.preferencesFlow.asLiveData().observeAsState()
    if (preferences == null) {
        BottomSheetBase(
            title = "Filtrera recept",
            subTitle = "Sortera och välj vilka recept som ska visas",
            buttonText = "Tillämpa",
            onClick = { /*Leave empty*/ }
        ) {
            LoadingScreen()
        }
    } else {
        val favoriteState = preferences!!.onlyFavorite

        val sortState = preferences!!.sortOrder
        val sortingAnswer = Answer.SingleChoice(sortState)

        val sliderPossibleAnswer = PossibleAnswer.Slider(range = 0f..100f, steps = 9)
        val durationStart = preferences!!.durationStart.toFloat() / (60*1000)
        val durationEnd = preferences!!.durationEnd.toFloat() / (60*1000)
        val sliderAnswer = Answer.Slider(answerRange = durationStart..durationEnd)

        FilterBottomSheetContent(
            sortingPossibleAnswer = sortOrderOptions,
            sortingAnswer = sortingAnswer,
            onSortingAnswerSelected = { homeViewModel.onSortOrderSelected(it) },
            favoriteState = favoriteState,
            onFavoriteCheckedChange = { homeViewModel.onOnlyFavoriteSelected(it) },
            sliderPossibleAnswer = sliderPossibleAnswer,
            sliderAnswer = sliderAnswer,
            onSliderAnswerSelected = {
                val end = if (it.endInclusive == sliderPossibleAnswer.range.endInclusive) 100000f else it.endInclusive
                homeViewModel.onDurationSelected(it.start, end)
            },
            onSaveClick = onSaveClick
        )
    }
}


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun FilterBottomSheetContent(
    sortingPossibleAnswer: PossibleAnswer.SingleChoice,
    sortingAnswer: Answer.SingleChoice,
    onSortingAnswerSelected: (Int) -> Unit,
    favoriteState: Boolean,
    onFavoriteCheckedChange: (Boolean) -> Unit,
    sliderPossibleAnswer: PossibleAnswer.Slider,
    sliderAnswer: Answer.Slider,
    onSliderAnswerSelected: (ClosedFloatingPointRange<Float>) -> Unit,
    onSaveClick: () -> Unit,
) {
    BottomSheetBase(
        title = "Filtrera recept",
        subTitle = "Sortera och välj vilka recept som ska visas",
        buttonText = "Tillämpa",
        onClick = onSaveClick
    ) {
        Column {
            SingleChoiceQuestion(
                title = "Sortera efter",
                possibleAnswer = sortingPossibleAnswer,
                answer = sortingAnswer,
                onAnswerSelected = { onSortingAnswerSelected(it) }
            )
            RecipeSpacer()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = favoriteState,
                    onCheckedChange = onFavoriteCheckedChange
                )
                RecipeSpacer()
                Text(text = "Endast favoritrecept")
            }
            RecipeSpacer()
            SliderQuestion(
                possibleAnswer = sliderPossibleAnswer,
                answer = sliderAnswer,
                onAnswerSelected = { onSliderAnswerSelected(it) }
            )
        }
    }
}
/**
 * The base for how a bottom sheet looks.
 */
@Composable
fun BottomSheetBase(
    title: String,
    subTitle: String,
    buttonText: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.h2)
        Text(text = subTitle, style = MaterialTheme.typography.body2)
        RecipeSpacer()
        content()
        RecipeSpacer()
        Button(onClick = onClick, modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)) {
            Text(text = buttonText)
        }
        RecipeSpacer()
    }
}

enum class BottomSheetCreateScreens { CATEGORY, PORTIONS, TIME }

@Composable
fun BottomSheetPortions(
    range: IntRange,
    selected: Int,
    setSelected: (Int) -> Unit,
    closeSheet: () -> Unit
) {
    BottomSheetBase(
        title = "Portioner",
        subTitle = "Välj antal portioner",
        buttonText = "Spara",
        onClick = closeSheet
    ) {
        NumberPicker(
            state = remember { mutableStateOf(selected) },
            range = range,
            modifier = Modifier.fillMaxWidth(),
            onStateChanged = setSelected
        )
    }
}

@Composable
fun BottomSheetTime(
    hourRange: IntRange,
    selectedHour: Int,
    setSelectedHour: (Int) -> Unit,
    minutesRange: IntRange,
    selectedMinute: Int,
    setSelectedMinute: (Int) -> Unit,
    closeSheet: () -> Unit
) {
    BottomSheetBase(
        title = "Tid",
        subTitle = "Hur lång tid tar det att laga?",
        buttonText = "Spara",
        onClick = closeSheet
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Timmar")
                NumberPicker(
                    state = remember { mutableStateOf(selectedHour) },
                    range = hourRange,
                    modifier = Modifier.fillMaxWidth(),
                    onStateChanged = setSelectedHour
                )
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Minuter")
                NumberPicker(
                    state = remember { mutableStateOf(selectedMinute) },
                    range = minutesRange,
                    modifier = Modifier.fillMaxWidth(),
                    onStateChanged = setSelectedMinute
                )
            }
        }
    }
}

@Composable
fun BottomSheetCategory(
    possibleAnswer: PossibleAnswer.MultipleChoice,
    answer: Answer.MultipleChoice?,
    onAnswerSelected: (String, Boolean) -> Unit,
    closeSheet: () -> Unit
) {
    val options = possibleAnswer.optionsStringList
    BottomSheetBase(
        title = "Kategori",
        subTitle = "Välj en kategori för det här receptet",
        buttonText = "Spara",
        onClick = closeSheet
    ) {
        Column {
            for (option in options) {
                var checkedState by remember {
                    val selectedOption = answer?.answersStringSet?.contains(option)
                    mutableStateOf(selectedOption ?: false)
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable {
                            checkedState = !checkedState
                            onAnswerSelected(option, checkedState)
                        }
                ) {
                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { selected ->
                            checkedState = selected
                            onAnswerSelected(option, selected)
                        }
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}