package com.strand.minarecept.ui.createAndEdit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.strand.minarecept.ui.components.RecipeTextField


/**
 * Create screen for web based recipes.
 */
@ExperimentalComposeUiApi
@Composable
fun CreateUrlScreen(
    createViewModel: CreateViewModel,
    contentPadding: PaddingValues
) {
    val url by createViewModel.url.observeAsState("")
    CreateUrlContent(
        url = url,
        onUrlChange = { createViewModel.onUrlChange(it) },
        contentPadding = contentPadding
    )
}

@ExperimentalComposeUiApi
@Composable
fun CreateUrlContent(
    url: String,
    onUrlChange: (String) -> Unit,
    contentPadding: PaddingValues
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .padding(contentPadding)
    ) {
        Text(text = "Ange länken till receptet du vill spara")
        RecipeTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "http://",
            label = "Länk"
        )
        // TODO: Show image of how to share via the browser (like in Whisk).
    }
}