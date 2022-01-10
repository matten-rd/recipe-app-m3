package com.strand.minarecept.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
fun RecipeTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    label: String = "",
    textStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
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
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() },
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = LocalContentColor.current.copy(LocalContentAlpha.current),
            disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current)
                .copy(ContentAlpha.disabled),
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.error,
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
                .copy(alpha = ContentAlpha.disabled),
            errorBorderColor = MaterialTheme.colorScheme.error,
            leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)
                .copy(alpha = ContentAlpha.disabled),
            errorLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
            trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)
                .copy(alpha = ContentAlpha.disabled),
            errorTrailingIconColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
                .copy(ContentAlpha.disabled),
            errorLabelColor = MaterialTheme.colorScheme.error,
            placeholderColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium),
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
                .copy(ContentAlpha.disabled)
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
        Text(text = header, style = MaterialTheme.typography.titleMedium)
        RecipeTextField(
            value = value ?: "",
            onValueChange = onValueChange,
            label = label
        )
    }
}


@ExperimentalComposeUiApi
@Composable
fun AutoCompleteSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onDoneActionClick: () -> Unit = {},
    onFocusChanged: (FocusState) -> Unit = {},
    onValueChanged: (String) -> Unit
) {
    SearchTextField(
        modifier = modifier.onFocusChanged { onFocusChanged(it) },
        value = value,
        onValueChange = { query ->
            onValueChanged(query)
        },
        onDoneActionClick = onDoneActionClick,
        placeholder = placeholder
    )
}


@ExperimentalComposeUiApi
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDoneActionClick: () -> Unit = {},
    placeholder: String = "",
    textStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
    cursorColor: Brush = SolidColor(MaterialTheme.colorScheme.onSurface),
    color: Color = MaterialTheme.colorScheme.onSurface,
    imeAction: ImeAction = ImeAction.Search,
    singleLine: Boolean = true
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp),
        cursorBrush = cursorColor,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onSearch = {
                onDoneActionClick()
                keyboardController?.hide()
            }
        ),
        singleLine = singleLine
    ) { innerTextField ->
        androidx.compose.material3.Surface(
            contentColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
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