package com.example.minarecept.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@Composable
fun SingleChoiceQuestion(
    title: String,
    possibleAnswer: PossibleAnswer.SingleChoice,
    answer: Answer.SingleChoice,
    onAnswerSelected: (Int) -> Unit
) {
    val options: Map<String, Int> = possibleAnswer.optionsStringResList.associateBy { stringResource(id = it) }
    val radioOptions = options.keys.toList()

    val selected = stringResource(id = answer.answerStringRes)

    val (selectedOption, onOptionSelected) = remember(answer) { mutableStateOf(selected) }

    ExpandingDetail(
        title = title,
        initialState = true,
        showDivider = false,
        textStyle = MaterialTheme.typography.body1,
        paddingValues = PaddingValues(0.dp)
    ) {
        Column(Modifier.selectableGroup().padding(horizontal = 16.dp)) {
            radioOptions.forEach { text ->
                val onClickHandle = {
                    onOptionSelected(text)
                    options[text]?.let { onAnswerSelected(it) }
                    Unit
                }
                val optionSelected = (text == selectedOption)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .selectable(
                            selected = optionSelected,
                            onClick = onClickHandle,
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = optionSelected,
                        onClick = null
                    )
                    RecipeSpacer()
                    Text(text = text, style = MaterialTheme.typography.body1)
                }
            }

        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SliderQuestion(
    possibleAnswer: PossibleAnswer.Slider,
    answer: Answer.Slider,
    onAnswerSelected: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(answer.answerRange) }

    Column {
        Row {
            Text(
                text = "Tillagningstid: " +
                        sliderPosition.start.roundToInt().toString() +
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
            onValueChangeFinished = { onAnswerSelected(sliderPosition) }
        )
    }


}