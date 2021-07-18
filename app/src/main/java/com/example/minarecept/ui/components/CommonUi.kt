package com.example.minarecept.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun RecipeSpacer() {
    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colors.primary)
    }
}


@Composable
fun DatePill(
    modifier: Modifier = Modifier,
    textDayOfWeek: String,
    numberDayOfWeek: Int,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (selected)
                    MaterialTheme.colors.primary
                else
                    MaterialTheme.colors.secondary
            )
            .size(width = 50.dp, height = 90.dp)
            .clickable(onClick = onSelected)
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = textDayOfWeek,
                style = MaterialTheme.typography.body1,
                color = if (selected)
                            MaterialTheme.colors.onPrimary
                        else
                            MaterialTheme.colors.onSecondary
            )
        }


        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$numberDayOfWeek")
        }

    }
}


@ExperimentalAnimationApi
@Composable
fun ExpandingDetail(
    title: String,
    initialState: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.h5,
    showDivider: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp),
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initialState) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .clickable { expanded = !expanded }
    ) {
        if (showDivider) {
            Divider(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(text = title, style = textStyle)
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )
        }
        AnimatedVisibility(visible = expanded, modifier = Modifier.padding(bottom = 8.dp)) {
            content()
        }
    }

}


/**
 * Simple component displaying some Text and a TextButton in a row filling max width with space between.
 */
@Composable
fun TextAndButton(
    text: String,
    buttonText: String = "ÄNDRA",
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        TextButton(onClick = onClick) {
            Text(text = buttonText)
        }
    }
}