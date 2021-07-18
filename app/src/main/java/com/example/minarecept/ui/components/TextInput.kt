package com.example.minarecept.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding

@ExperimentalComposeUiApi
@Composable
fun RecipeTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    label: String = "",
    textStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colors.primary),
    imeAction: ImeAction = ImeAction.Done,
    trailingIcon: @Composable () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder) },
        label = { Text(text = label) },
        textStyle = textStyle,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        )
    ) 
}

@ExperimentalComposeUiApi
@Composable
fun TextFieldWithHeader(
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    header: String
) {
    Column {
        Text(text = header, style = MaterialTheme.typography.h6)
        RecipeTextField(
            value = value ?: "",
            onValueChange = onValueChange,
            label = label
        )
    }
}


@ExperimentalComposeUiApi
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    textStyle: TextStyle = MaterialTheme.typography.subtitle2.copy(color = MaterialTheme.colors.primary),
    cursorColor: Brush = SolidColor(MaterialTheme.colors.primary),
    color: Color = MaterialTheme.colors.primary,
    imeAction: ImeAction = ImeAction.Search,
    singleLine: Boolean = true
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .navigationBarsWithImePadding(),
        cursorBrush = cursorColor,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onSearch = { keyboardController?.hide() }
        ),
        singleLine = singleLine
    ) { innerTextField ->
        Surface(
            contentColor = MaterialTheme.colors.surface,
            modifier = Modifier.height(56.dp),
            shape = CircleShape,
            elevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = textStyle.copy(color = color.copy(0.7f)))
                    }
                    SelectionContainer {
                        innerTextField()
                    }
                }
            }
        }

    }
}