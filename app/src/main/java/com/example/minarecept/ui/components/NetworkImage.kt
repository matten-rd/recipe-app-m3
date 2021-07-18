package com.example.minarecept.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@ExperimentalCoilApi
@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = MaterialTheme.colors.secondary
) {
    Box(modifier = modifier) {
        val painter = rememberImagePainter(
            data = url,
            builder = {
                crossfade(true)
            }
        )
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
        when (painter.state) {
            is ImagePainter.State.Success -> {

            }
            is ImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(placeholderColor),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onSecondary)
                }
            }
            is ImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(placeholderColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            }
        }
    }
}


@ExperimentalCoilApi
@Composable
fun EditImage(
    url: String?,
    setImage: () -> Unit,
    header: String
) {
    Column {
        Text(text = header, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(4.dp))
        NetworkImage(
            url = url ?: "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable(onClick = setImage)
        )
    }
}