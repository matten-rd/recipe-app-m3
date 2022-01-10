package com.strand.minarecept.ui.components

/**
 * Store possible answers.
 */
sealed class PossibleAnswer {
    data class SingleChoice(val optionsStringResList: List<Int>) : PossibleAnswer()
    data class MultipleChoice(val optionsStringList: List<String>) : PossibleAnswer()

    data class Slider(
        val range: ClosedFloatingPointRange<Float>,
        val steps: Int
    ) : PossibleAnswer()
}

/**
 * Store selected answers.
 */
sealed class Answer<T : PossibleAnswer> {
    data class SingleChoice(val answerStringRes: Int) : Answer<PossibleAnswer.SingleChoice>()
    data class MultipleChoice(val answersStringSet: List<String>) : Answer<PossibleAnswer.MultipleChoice>()

    data class Slider(
        val answerRange: ClosedFloatingPointRange<Float>
    ) : Answer<PossibleAnswer.Slider>()
}

/**
 * Add or remove an answer from the list of selected answers depending on whether the answer was
 * selected or deselected.
 */
fun Answer.MultipleChoice.withAnswerSelected(
    answer: String,
    selected: Boolean
): Answer.MultipleChoice {
    val newStringSet = answersStringSet.toMutableList()
    if (!selected) {
        newStringSet.remove(answer)
    } else {
        newStringSet.add(answer)
    }
    return Answer.MultipleChoice(newStringSet)
}