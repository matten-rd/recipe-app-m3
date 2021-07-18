package com.example.minarecept.ui.detail

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.data.local.Recipe
import com.example.minarecept.ui.components.*
import com.example.minarecept.util.getDomainName
import com.example.minarecept.util.humanReadableDuration
import com.example.minarecept.util.isValidUrl
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun DetailScreenState(
    recipeId: Int?,
    navigateUp: () -> Unit,
    navigateToEditScreen: () -> Unit,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    if (recipeId != null) {
        val currentRecipe by detailViewModel.loadCurrentRecipe(recipeId).observeAsState()
        if (currentRecipe != null) {
            DetailScreen(
                recipe = currentRecipe!!,
                navigateUp = navigateUp,
                navigateToEditScreen = navigateToEditScreen,
                scope = scope,
                scaffoldState = scaffoldState
            )
        } else {
            LoadingScreen()
        }
    } else {
        // TODO: show error screen
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun DetailScreen(
    recipe: Recipe,
    navigateUp: () -> Unit,
    navigateToEditScreen: () -> Unit,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val onShowSnackbar: (Recipe) -> Unit = { recipeSnack ->
        scope.launch {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = "${recipeSnack.title} raderat.",
                actionLabel = "ÅNGRA",
                duration = SnackbarDuration.Long
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> Log.d("snackbarResult", "Dismissed")
                SnackbarResult.ActionPerformed -> detailViewModel.insertRecipe(recipeSnack)
            }
        }
    }

     val intent = if (isValidUrl(recipe.recipeUrl)) {
        remember {
            Intent(Intent.ACTION_VIEW, Uri.parse(recipe.recipeUrl))
        }
    } else {
        Intent()
     }
    DetailContent(
        recipeUrl = recipe.recipeUrl,
        image = recipe.thumbnailImage,
        title = recipe.title,
        categories = recipe.category ?: emptySet(),
        description = recipe.description ?: "",
        isFavorite = recipe.isFavorite,
        recipeYield = recipe.yield ?: 0,
        ingredients = recipe.ingredients,
        instructions = recipe.instructions,
        recipeImage = recipe.recipeImage,
        time = humanReadableDuration(recipe.totalTime),
        onDeleteClick = {
            onShowSnackbar(recipe) // FIXME: This doesn't work
            detailViewModel.deleteRecipe(recipe)
            navigateUp()
        },
        onLikeClick = {
            detailViewModel.onLikeClick(recipe)
        },
        onOpenInBrowserClick = {
            context.startActivity(intent)
        },
        navigateToEditScreen = navigateToEditScreen
    )

}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun DetailContent(
    recipeUrl: String?,
    image: String,
    title: String,
    categories: Set<String>,
    description: String,
    isFavorite: Boolean,
    recipeYield: Int,
    ingredients: List<String>?,
    instructions: List<String>?,
    recipeImage: String?,
    time: String,
    onDeleteClick: () -> Unit,
    onLikeClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    navigateToEditScreen: () -> Unit
) {
    var overflowMenuExpanded by remember { mutableStateOf(false) }
    /**
     * Thumbnail image and portions, like button and openInBrowser button.
     */
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            NetworkImage(
                url = image,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colors.secondary,
                            shape = RoundedCornerShape(topEnd = 28.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(bottom = 0.dp, top = 4.dp, start = 12.dp, end = 12.dp)
                    ) {
                        Text(
                            text = "$recipeYield", 
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSecondary,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
                Row(modifier = Modifier.padding(12.dp)) {
                    if (isValidUrl(recipeUrl)) {
                        SmallIconButton(imageVector = Icons.Rounded.OpenInNew) {
                            onOpenInBrowserClick()
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    SmallIconButton(
                        imageVector = if (isFavorite)
                            Icons.Rounded.Favorite
                        else
                            Icons.Rounded.FavoriteBorder
                    ) {
                        onLikeClick()
                    }
                }
            }
        }

        /**
         * Recipe title and overflowMenu.
         */
        Row(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(text = title, style = MaterialTheme.typography.h3, modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = { overflowMenuExpanded = true }) {
                    Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                }
                DropdownMenu(
                    expanded = overflowMenuExpanded,
                    onDismissRequest = { overflowMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    DropdownMenuItem(onClick = navigateToEditScreen) {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
                        RecipeSpacer()
                        Text(text = "Redigera")
                    }
                    DropdownMenuItem(onClick = onDeleteClick) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                        RecipeSpacer()
                        Text(text = "Radera")
                    }
                }
            }

        }

        /**
         * Time and categories.
         */
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            background = MaterialTheme.colors.primary.copy(alpha = 0.5f),
            fontSize = 16.sp
        )
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            crossAxisSpacing = 8.dp,
            mainAxisSpacing = 8.dp
        ) {
            if (time.isNotEmpty()) {
                Text(text = time, fontSize = 16.sp)
            }
            if (time.isNotEmpty() && categories.isNotEmpty()) {
                Text(text = "•", fontSize = 16.sp)
            }
            categories.forEach { category ->
                Text(
                    text = buildAnnotatedString {
                        withStyle(tagStyle) {
                            append(" $category ")
                        }
                    }
                )
            }
        }
        RecipeSpacer()

        /**
         * Recipe description.
         */
        Text(
            text = description,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        RecipeSpacer()

        /**
         * Show ingredient list if it's not null.
         */
        if (ingredients != null) {
            ExpandingDetail(title = "Ingredienser") {
                Column {
                    ingredients.forEach { ingredient ->
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        /**
         * Show instruction list if it's not null.
         */
        if (instructions != null) {
            ExpandingDetail(title = "Instruktioner") {
                Column {
                    instructions.forEachIndexed { index, instruction ->
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colors.primaryVariant,
                                        shape = CircleShape
                                    )
                                    .size(28.dp)
                            ) {
                                Text(
                                    text = String.format("%02d", index+1),
                                    style = MaterialTheme.typography.body1
                                )
                            }
                            RecipeSpacer()
                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }

        /**
         * Show webview if it's a web recipe (not image recipe).
         */
        if (isValidUrl(recipeUrl)) {
            val domainName = getDomainName(recipeUrl!!)
            ExpandingDetail(title = domainName) {
                Row(
                    modifier = Modifier
                ) {
                    AndroidView(factory = {
                        WebView(it).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadsImagesAutomatically = true
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                            webChromeClient = WebChromeClient()
                            loadUrl(recipeUrl)
                        }
                    }, update = {
                        it.loadUrl(recipeUrl)
                    })
                }
            }
        }

        /**
         * Show recipe image if it's not null.
         */
        if (recipeImage != null) {
            ExpandingDetail(title = "Receptbild") {
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                    scale *= zoomChange
                    offset += offsetChange
                }

                NetworkImage(
                    url = recipeImage,
                    modifier = Modifier
                        .height(700.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = state)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { scale = 1f; offset = Offset.Zero }
                            )
                        },
                    contentScale = ContentScale.Fit
                )

            }
        }

    }
}