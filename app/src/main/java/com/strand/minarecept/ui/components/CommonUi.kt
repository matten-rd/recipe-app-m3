package com.strand.minarecept.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.strand.minarecept.R
import java.time.LocalDate

@Composable
fun RecipeSpacer() {
    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorScreen(errorMessage: String = stringResource(id = R.string.error_general)) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = errorMessage)
    }
}


@Composable
fun DatePill(
    modifier: Modifier = Modifier,
    localDateObject: LocalDate,
    textDayOfWeek: String,
    numberDayOfWeek: Int,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(35))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.primary
            )
            .size(width = 45.dp, height = 90.dp)
            .clickable(onClick = onSelected)
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(35))
                .size(35.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = textDayOfWeek,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected)
                            MaterialTheme.colorScheme.onTertiary
                        else
                            MaterialTheme.colorScheme.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(35))
                .size(35.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$numberDayOfWeek",
                color = if (localDateObject == LocalDate.now())
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.onBackground
            )
        }

    }
}


@ExperimentalAnimationApi
@Composable
fun ExpandingDetail(
    title: String,
    initialState: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
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
            Divider(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
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


@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.horizontalSwipe(
    onLeft: () -> Unit,
    onRight: () -> Unit
): Modifier = composed {
    // TODO: hoist the offset and move the week view according to the delta offset
    val offset = remember { mutableStateOf(0f) }
    draggable(
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { delta ->
            offset.value += delta
        },
        onDragStopped = { velocity ->
            if (velocity > 500 && offset.value > 50) {
                onLeft()
            } else if (velocity < -500 && offset.value < -50) {
                onRight()
            }
        }
    )
}