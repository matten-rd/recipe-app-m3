package com.example.minarecept.ui.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.ui.components.*

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@Composable
fun HomeScreen(
    contentPadding: PaddingValues
) {
    val horizontalPadding = Modifier.padding(horizontal = 16.dp)
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                SearchTextField(
                    value = "",
                    onValueChange = {  },
                    placeholder = "Sök...",
                    modifier = Modifier.weight(1f)
                )
                RecipeSpacer()
                RecipeFab(
                    icon = { Icon(imageVector = Icons.Rounded.FilterList, contentDescription = null, modifier = Modifier.size(40.dp)) },
                    onClick = { /*TODO*/ }
                )
            }
        }

        item {
            Text(text = "Kategorier", style = MaterialTheme.typography.h5, modifier = horizontalPadding)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(10) { index ->
                    CarouselCard(
                        recipeId = 0,
                        modifier =
                            if (index == 0)
                                Modifier.padding(start = 16.dp)
                            else if(index == 9)
                                Modifier.padding(end = 16.dp)
                            else
                                Modifier
                            ,
                        url = "https://picsum.photos/600/600",
                        title = "Fikabröd $index",
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
        item { RecipeSpacer() }

        item {
            Text(text = "Populära recept", style = MaterialTheme.typography.h3, modifier = horizontalPadding)
        }

        items(20) { index ->
            DiscoverCard(
                recipeId = 0,
                modifier = horizontalPadding.padding(bottom = 16.dp),
                url = "https://picsum.photos/600/600",
                title = "Hamburgare: $index",
                isLiked = false,
                onClick = { /*TODO*/ },
                onLikeClick = { /*TODO*/ }
            )
        }


    }
}