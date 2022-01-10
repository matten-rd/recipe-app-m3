package com.strand.minarecept.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.HighlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction


/**
 * A list with your choice of composable that is fully editable.
 * Supports adding and deleting (with snackbar undo).
 * (Possible to support editing current items).
 */
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun EditableList(
    header: String,
    label: String,
    list: List<String>?,
    onValueChange: (String, String) -> Unit,
    addToList: () -> Unit,
    deleteFromList: (Int) -> Unit,
    newValue: String,
    setNewValue: (String) -> Unit,
    onShowSnackbar: (Int, String) -> Unit,
    listHeaders: @Composable (Int) -> Unit = {},
    listItems: @Composable (String) -> Unit
) {
    var checked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = header, style = MaterialTheme.typography.headlineMedium)
            TextToggleButton(
                checked = checked,
                onCheckedChange = { checked = it },
                text = if (checked) "KLAR" else "REDIGERA"
            )
        }
        AnimatedContent(targetState = checked) { isEdit ->
            if (isEdit)
                ContentEdit(
                    list = list,
                    delete = deleteFromList,
                    onValueChange = onValueChange,
                    onShowSnackbar = onShowSnackbar,
                    header = { listHeaders(it) }
                )
            else
                Column {
                    list?.forEachIndexed { index, item ->
                        listHeaders(index)
                        listItems(item)
                    }
                }
        }

        RecipeSpacer()
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                RecipeTextField(
                    value = newValue, 
                    onValueChange = setNewValue, 
                    label = label,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = addToList) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    }
}


@ExperimentalComposeUiApi
@Composable
fun ContentEdit(
    list: List<String>?,
    delete: (Int) -> Unit,
    onValueChange: (String, String) -> Unit,
    onShowSnackbar: (Int, String) -> Unit,
    header: @Composable (Int) -> Unit = {}
) {
    Column {
        list?.forEachIndexed { index, item ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    delete(index)
                    onShowSnackbar(index, item)
                }) {
                    Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null)
                }
                Column(Modifier.weight(1f)) {
                    header(index)
                    RecipeTextField(
                        value = item,
                        onValueChange = { onValueChange(item, it) },
                        imeAction = ImeAction.Next,
                        trailingIcon = {
                            IconButton(onClick = { onValueChange(item, "") }) {
                                Icon(imageVector = Icons.Rounded.HighlightOff, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    }
}