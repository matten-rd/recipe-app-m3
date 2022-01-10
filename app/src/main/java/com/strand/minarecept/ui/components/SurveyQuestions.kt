package com.strand.minarecept.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Removes all ripple effects.
 */
private object RippleCustomTheme: RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Transparent

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleAlpha(0f, 0f, 0f, 0f)
}

@ExperimentalAnimationApi
@Composable
internal fun SingleChoiceQuestion(
    title: String,
    possibleAnswer: PossibleAnswer.SingleChoice,
    answer: Answer.SingleChoice,
    onAnswerSelected: (Int) -> Unit
) {
    val options: Map<String, Int> = possibleAnswer.optionsStringResList
        .associateBy { stringResource(id = it) }
    val radioOptions = options.keys.toList()

    val selected = stringResource(id = answer.answerStringRes)

    val (selectedOption, onOptionSelected) = remember(answer) { mutableStateOf(selected) }

    Column(modifier = Modifier.selectableGroup()) {
        Text(
            text = title.uppercase(),
            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
            style = MaterialTheme.typography.labelMedium
        )

        radioOptions.forEach { text ->
            val onClickHandle = {
                onOptionSelected(text)
                options[text]?.let { onAnswerSelected(it) }
                Unit
            }
            val optionSelected = (text == selectedOption)
            CompositionLocalProvider(LocalRippleTheme provides RippleCustomTheme) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle,
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RecipeRadioButton(
                        selected = optionSelected,
                        onClick = null
                    )
                    RecipeSpacer()
                    Text(text = text)
                }
            }
        }
    }
}

@Composable
internal fun MultipleChoiceQuestion(
    possibleAnswer: PossibleAnswer.MultipleChoice,
    answer: Answer.MultipleChoice?,
    onAnswerSelected: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = possibleAnswer.optionsStringList
    Column(modifier = modifier.selectableGroup()) {
        for (option in options) {
            var checkedState by remember(answer) {
                val selectedOption = answer?.answersStringSet?.contains(option)
                mutableStateOf(selectedOption ?: false)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(
                        onClick = {
                            checkedState = !checkedState
                            onAnswerSelected(option, checkedState)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RecipeCheckbox(
                    checked = checkedState,
                    onCheckedChange = null
                )
                RecipeSpacer()
                Text(text = option)
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
internal fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider,
    onAnswerSelected: (ClosedFloatingPointRange<Float>) -> Unit
) {
    // Recalculate sliderPosition when answer changes.
    // This is important because otherwise it will only be calculated during the first composition
    // which will just contain an initial set of preferences and not the correct ones.
    var sliderPosition by remember(answer) { mutableStateOf(answer.answerRange) }
    Column {
        Row {
            Text(
                text = sliderPosition.start.roundToInt().toString() +
                        " - " +
                        sliderPosition.endInclusive.roundToInt().toString() +
                        if (sliderPosition.endInclusive == possibleAnswer.range.endInclusive) "+ min" else " min"
            )
        }
        RangeSlider(
            values = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = possibleAnswer.range,
            steps = possibleAnswer.steps,
            modifier = Modifier.weight(1f),
            onValueChangeFinished = { onAnswerSelected(sliderPosition) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
            )
        )
    }


}