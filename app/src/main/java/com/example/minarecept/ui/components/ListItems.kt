package com.example.minarecept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.ui.theme.BlackBlur
import com.example.minarecept.ui.theme.White
import com.example.minarecept.ui.theme.WhiteBlur

/**
 * A big discover card displayed on the home screen.
 */
@ExperimentalCoilApi
@Composable
fun DiscoverCard(
    recipeId: Int,
    modifier: Modifier = Modifier,
    url: String,
    title: String,
    isLiked: Boolean,
    onClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Surface(elevation = 8.dp, shape = MaterialTheme.shapes.large, modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable(onClick = onClick)
        ) {
            val (imageRef, titleRef, likeRef) = createRefs()

            NetworkImage(
                url = url,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(imageRef) {
                        centerTo(parent)
                    }
            )

            DiscoverCardLike(
                isLiked = isLiked,
                onLikeClick = onLikeClick,
                modifier = Modifier
                    .constrainAs(likeRef) {
                        top.linkTo(parent.top, margin = 12.dp)
                        end.linkTo(parent.end, margin = 12.dp)
                    }
            )

            DiscoverCardTitle(
                title = title,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(0.4f)
                    .constrainAs(titleRef) {
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    }
            )
        }
    }
}

@Composable
private fun DiscoverCardTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(
                if (MaterialTheme.colors.isLight)
                    WhiteBlur
                else
                    BlackBlur
            )
            .fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DiscoverCardLike(
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (MaterialTheme.colors.isLight)
                    WhiteBlur
                else
                    BlackBlur
            )
            .size(36.dp)
            .clickable(onClick = onLikeClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLiked)
            Icon(imageVector = Icons.Outlined.Favorite, contentDescription = null)
        else
            Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = null)
    }
}


/**
 * A compact list item used in the my recipes page and planning page.
 * For the planning page set isLiked to false and pass in the edit Icon.
 */
@ExperimentalCoilApi
@Composable
fun CompactListItem(
    recipeId: Int,
    modifier: Modifier = Modifier,
    url: String,
    title: String,
    isLiked: Boolean,
    onClick: () -> Unit,
    onIconClick: () -> Unit,
    trailingIcon: @Composable () -> Unit = { Icon(
        imageVector = Icons.Rounded.FavoriteBorder,
        contentDescription = null,
        tint = MaterialTheme.colors.primary
    ) }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
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
                    .clip(MaterialTheme.shapes.small)
            )
            RecipeSpacer()
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onIconClick) {
            if (isLiked)
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            else
                trailingIcon()
        }
    }
}


@ExperimentalCoilApi
@Composable
fun CarouselCard(
    recipeId: Int,
    modifier: Modifier = Modifier,
    url: String,
    title: String,
    contentAlignment: Alignment = Alignment.Center,
    maxLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.h3,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(width = 150.dp, height = 100.dp)
            .clickable(onClick = onClick),
        contentAlignment = contentAlignment
    ) {
        NetworkImage(
            url = url,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .fillMaxSize()
        )

        Text(
            text = title,
            style = textStyle,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            color = White
        )

    }
}


