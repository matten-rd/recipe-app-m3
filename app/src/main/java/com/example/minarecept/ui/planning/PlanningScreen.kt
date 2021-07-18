package com.example.minarecept.ui.planning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.ui.components.CompactListItem
import com.example.minarecept.ui.components.DatePill
import com.example.minarecept.ui.components.RecipeSpacer

@ExperimentalCoilApi
@Composable
fun PlanningScreen(
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .padding(top = 16.dp)
            .fillMaxSize()
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(31) { index ->
                DatePill(
                    modifier = if (index == 0)
                                    Modifier.padding(start = 16.dp)
                                else if(index == 30)
                                    Modifier.padding(end = 16.dp)
                                else
                                    Modifier,
                    textDayOfWeek = "Mån",
                    numberDayOfWeek = index,
                    selected = index == 4,
                    onSelected = {  }
                )
            }
        }
        RecipeSpacer()

        Text(text = "Måndag, 24 Maj", style = MaterialTheme.typography.h4, modifier = Modifier.padding(horizontal = 16.dp))

        RecipeSpacer()
        Text(text = "FRUKOST", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(horizontal = 16.dp))
        CompactListItem(
            recipeId = 0,
            modifier = Modifier.padding(horizontal = 16.dp),
            url = "https://picsum.photos/600/600",
            title = "Kanelbullar",
            isLiked = false,
            onClick = { /*TODO*/ },
            onIconClick = {  },
            trailingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
        )

        RecipeSpacer()
        Text(text = "LUNCH", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(horizontal = 16.dp))
        CompactListItem(
            recipeId = 0,
            modifier = Modifier.padding(horizontal = 16.dp),
            url = "https://picsum.photos/600/600",
            title = "Kanelbullar",
            isLiked = false,
            onClick = { /*TODO*/ },
            onIconClick = {  },
            trailingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
        )

        RecipeSpacer()
        Text(text = "MIDDAG", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(horizontal = 16.dp))
        CompactListItem(
            recipeId = 0,
            modifier = Modifier.padding(horizontal = 16.dp),
            url = "https://picsum.photos/600/600",
            title = "Kanelbullar",
            isLiked = false,
            onClick = { /*TODO*/ },
            onIconClick = {  },
            trailingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) }
        )
    }
}