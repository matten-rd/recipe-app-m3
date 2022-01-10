package com.strand.minarecept.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.strand.minarecept.R
import com.strand.minarecept.data.local.FilterPreferences
import com.strand.minarecept.data.local.categoriesOptions
import com.strand.minarecept.data.local.sortOrderOptions
import com.strand.minarecept.ui.planning.PlanningViewModel
import com.strand.minarecept.ui.saved.SavedViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun AddNewRecipeBottomSheet(
    navigateToNewUrlRecipe: () -> Unit,
    navigateToNewImageRecipe: () -> Unit,
    navigateToPhotoActivity: () -> Unit
) {
    BottomSheetBase(
        title = stringResource(id = R.string.new_recipe),
        enableButton = false
    ) {
        OutlinedButton(
            onClick = { navigateToNewUrlRecipe() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.AddLink,
                contentDescription = null,
            )
            RecipeSpacer()
            Text(
                text = stringResource(id = R.string.save_recipe_link)
            )
        }
        RecipeSpacer()
        OutlinedButton(
            onClick = { navigateToNewImageRecipe() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.AddPhotoAlternate,
                contentDescription = null,
            )
            RecipeSpacer()
            Text(
                text = stringResource(id = R.string.save_recipe_image)
            )
        }
        RecipeSpacer()
        OutlinedButton(
            onClick = { navigateToPhotoActivity() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = null,
            )
            RecipeSpacer()
            Text(
                text = stringResource(id = R.string.take_picture_recipe)
            )
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun FilterBottomSheet(
    onSaveClick: () -> Unit,
    preferences: FilterPreferences,
    savedViewModel: SavedViewModel = hiltViewModel<SavedViewModel>()
) {
    val favoriteState = preferences.onlyFavorite

    val sortState = preferences.sortOrder
    val sortingAnswer = Answer.SingleChoice(sortState)

    val sliderPossibleAnswer = PossibleAnswer.Slider(range = 0f..100f, steps = 9)
    val durationStart = preferences.durationStart.toFloat() / (60*1000)
    val durationEnd = preferences.durationEnd.toFloat() / (60*1000)
    val sliderAnswer = Answer.Slider(answerRange = durationStart..durationEnd)

    val categoriesAnswer = Answer.MultipleChoice(preferences.categories.toList())

    FilterBottomSheetContent(
        sortingPossibleAnswer = sortOrderOptions,
        sortingAnswer = sortingAnswer,
        onSortingAnswerSelected = { savedViewModel.onSortOrderSelected(it) },
        favoriteState = favoriteState,
        onFavoriteCheckedChange = { savedViewModel.onOnlyFavoriteSelected(it) },
        sliderPossibleAnswer = sliderPossibleAnswer,
        sliderAnswer = sliderAnswer,
        onSliderAnswerSelected = {
            val end =
                if (it.endInclusive == sliderPossibleAnswer.range.endInclusive)
                    100000f
                else
                    it.endInclusive
            savedViewModel.onDurationSelected(it.start, end)
        },
        categoryPossibleAnswer = categoriesOptions,
        categoryAnswer = categoriesAnswer,
        onCategoryAnswerSelected = { newAnswer, selected ->
            savedViewModel.onCategoriesSelected(categoriesAnswer, newAnswer, selected)
        },
        onSaveClick = onSaveClick
    )

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
    categoryPossibleAnswer: PossibleAnswer.MultipleChoice,
    categoryAnswer: Answer.MultipleChoice?,
    onCategoryAnswerSelected: (String, Boolean) -> Unit,
    onSaveClick: () -> Unit,
) {
    BottomSheetBase(
        title = stringResource(id = R.string.filter_and_sort),
        subTitle = stringResource(id = R.string.filter_and_sort_description),
        buttonText = stringResource(id = R.string.apply),
        onClick = onSaveClick
    ) {
        Column(modifier = Modifier.wrapContentHeight()) {
            Column {
                Text(
                    text = stringResource(id = R.string.favorites).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RecipeCheckbox(
                        checked = favoriteState,
                        onCheckedChange = onFavoriteCheckedChange
                    )
                    RecipeSpacer()
                    Text(text = stringResource(id = R.string.only_favorites))
                }
                RecipeSpacer()
                Text(
                    text = stringResource(id = R.string.time).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                SliderQuestion(
                    possibleAnswer = sliderPossibleAnswer,
                    answer = sliderAnswer,
                    onAnswerSelected = { onSliderAnswerSelected(it) }
                )
                Text(
                    text = stringResource(id = R.string.categories).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                MultipleChoiceQuestion(
                    possibleAnswer = categoryPossibleAnswer,
                    answer = categoryAnswer,
                    onAnswerSelected = onCategoryAnswerSelected
                )
            }
            RecipeSpacer()
            SingleChoiceQuestion(
                title = stringResource(id = R.string.sort_by),
                possibleAnswer = sortingPossibleAnswer,
                answer = sortingAnswer,
                onAnswerSelected = { onSortingAnswerSelected(it) }
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun BottomSheetAddMealPlan(
    saveClick: () -> Unit,
    title: String = "Lägg till recept",
    planningViewModel: PlanningViewModel = hiltViewModel()
) {
    val mealPlanText by planningViewModel.mealPlanText.observeAsState(initial = "")

    BottomSheetBase(
        title = title,
        subTitle = "Skriv in text eller välj ett recept",
        buttonText = "Lägg till",
        onClick = saveClick
    ) {
        RecipeTextField(
            value = mealPlanText,
            onValueChange = { planningViewModel.onMealPlanTextChange(it) },
            modifier = Modifier.navigationBarsWithImePadding(),
            label = title,
            placeholder = title
        )
    }
}

/**
 * The base for how a bottom sheet looks.
 */
@Composable
fun BottomSheetBase(
    title: String,
    subTitle: String? = null,
    buttonText: String = "",
    onClick: () -> Unit = {},
    enableButton: Boolean = true,
    content: @Composable () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 0.dp, start = 20.dp, end = 20.dp)
            .heightIn(max = 750.dp)

    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(
                thickness = 4.dp,
                modifier = Modifier
                    .width(56.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
            RecipeSpacer()
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subTitle != null) {
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            RecipeSpacer()
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                content()
            }
            RecipeSpacer()

        }
        if (enableButton) {
            RecipeButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = buttonText)
            }
            RecipeSpacer()
        }
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
        buttonText = stringResource(id = R.string.save),
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
        buttonText = stringResource(id = R.string.save),
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
    BottomSheetBase(
        title = "Kategori",
        subTitle = "Välj en kategori för det här receptet",
        buttonText = stringResource(id = R.string.save),
        onClick = closeSheet
    ) {
        MultipleChoiceQuestion(
            possibleAnswer = possibleAnswer,
            answer = answer,
            onAnswerSelected = onAnswerSelected
        )
    }
}