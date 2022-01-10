package com.strand.minarecept.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi


/**
 * A compact list item used in the my recipes page and planning page.
 * For the planning page set isLiked to false and pass in the edit Icon.
 */
@ExperimentalCoilApi
@Composable
fun CompactListItem(
    modifier: Modifier = Modifier,
    url: Any?,
    title: String,
    isLiked: Boolean,
    onClick: () -> Unit = {},
    onClickEnabled: Boolean = true,
    onIconClick: () -> Unit,
    trailingIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Rounded.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick, enabled = onClickEnabled),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            NetworkImage(
                url = url,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            RecipeSpacer()
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(onClick = onIconClick) {
            if (isLiked)
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            else
                trailingIcon()
        }

    }
}


@ExperimentalMaterialApi
@Composable
fun ListCard(
    spacing: Dp = 2.dp,
    leftCompMinSize: Dp = 70.dp,
    text: String,
    onClick: () -> Unit = {},
    leftComp: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.defaultMinSize(minWidth = leftCompMinSize)) {
                leftComp()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun IngredientListCard(
    ingredient: String,
    amount: String,
) {
    ListCard(text = ingredient.trim()) {
        Text(
            text = if (amount.isNotBlank()) amount else "-",
            fontWeight = FontWeight.Bold
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun InstructionListCard(
    instruction: String
) {
    val checkedState = remember { mutableStateOf(false) }
    ListCard(
        spacing = 4.dp,
        leftCompMinSize = 0.dp,
        text = instruction.trim(),
        onClick = { checkedState.value = !checkedState.value }
    ) {
        RecipeCheckbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it },
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun MealPlanListCard(
    mealType: String,
    mealTitle: String,
    color: Color = MaterialTheme.colorScheme.primary,
    editOnClick: () -> Unit,
    clearOnClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(text = mealType, style = MaterialTheme.typography.headlineMedium)
                    Divider(modifier = Modifier.padding(vertical = 24.dp))
                }
                Canvas(modifier = Modifier.weight(1f)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    // TODO: Draw image of the selected recipe here if recipe was selected
                    drawCircle(
                        color = color,
                        center = Offset(x = canvasWidth*0.9f, y = canvasHeight*0.1f),
                        radius = canvasWidth
                    )
                }
            }

            Text(text = mealTitle, style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
            ) {
                FilledTonalButton(
                    onClick = clearOnClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Rensa")
                }
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                FilledTonalButton(
                    onClick = editOnClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Ändra")
                }
            }
        }
    }
}


