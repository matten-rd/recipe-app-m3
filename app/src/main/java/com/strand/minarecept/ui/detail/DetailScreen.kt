package com.strand.minarecept.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.flowlayout.FlowRow
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.ui.components.*
import com.strand.minarecept.ui.navigation.RecipeTopAppBar
import com.strand.minarecept.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun DetailScreenState(
    recipeId: String,
    navigateUp: () -> Unit,
    navigateToEditScreen: (String) -> Unit,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val firebaseRecipe by detailViewModel.recipe.collectAsState()
    when (firebaseRecipe) {
        is UiResult.Success<FirebaseRecipe> -> {
            (firebaseRecipe as UiResult.Success<FirebaseRecipe>).data?.let {
                DetailScreen(
                    recipe = it,
                    navigateUp = navigateUp,
                    navigateToEditScreen = { navigateToEditScreen(recipeId) }
                )
            }
        }
        is UiResult.Loading -> {
            LoadingScreen()
        }
        is UiResult.Failure<*> -> {
            // TODO: Display error screen / error snackbar
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun DetailScreen(
    recipe: FirebaseRecipe,
    navigateUp: () -> Unit,
    navigateToEditScreen: () -> Unit,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val intent = if (isValidUrl(recipe.recipeUrl)) {
        remember { Intent(Intent.ACTION_VIEW, Uri.parse(recipe.recipeUrl)) }
    } else {
        remember { Intent() }
    }
    val scrollState = rememberScrollState()
    // FIXME: Look for inefficiencies
    //  and probably look into how to create a rememberDetailState() composable
    //  to hold state more efficiently for this screen
    //  note that only 'isFavorite' state ever change here
    DetailContent(
        recipeUrl = recipe.recipeUrl,
        image = recipe.thumbnailImage,
        title = recipe.title,
        categories = recipe.category ?: emptyList(),
        description = recipe.description ?: "",
        isFavorite = recipe.isFavorite,
        recipeYield = recipe.yield ?: 0,
        ingredients = recipe.ingredients,
        instructions = recipe.instructions,
        recipeImage = recipe.recipeImage,
        time = humanReadableDuration(recipe.totalTime),
        onDeleteClick = {
            detailViewModel.firebaseDeleteRecipe(recipe.recipeId)
            navigateUp()
        },
        onLikeClick = { detailViewModel.firebaseOnLikeClick(recipe.isFavorite, recipe.recipeId) },
        onOpenInBrowserClick = { context.startActivity(intent) },
        navigateToEditScreen = navigateToEditScreen,
        navigateUp = navigateUp,
        scrollState = scrollState
    )

}

@ExperimentalMaterial3Api
@SuppressLint("SetJavaScriptEnabled")
@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun DetailContent(
    recipeUrl: String?,
    image: String,
    title: String,
    categories: List<String>,
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
    navigateToEditScreen: () -> Unit,
    navigateUp: () -> Unit,
    scrollState: ScrollState
) {
    var overflowMenuExpanded by remember { mutableStateOf(false) }
    val openAlertDialog = remember { mutableStateOf(false) }

    /**
     * Delete AlertDialog.
     */
    if (openAlertDialog.value) {
        AlertDialog(
            onDismissRequest = { openAlertDialog.value = false },
            title = { Text(text = "Radera recept?") },
            text = { Text(text = "Är du säker? Det går inte att ångra.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        openAlertDialog.value = false
                    }
                ) {
                    Text(text = "Radera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAlertDialog.value = false }
                ) {
                    Text(text = "Avbryt")
                }
            }
        )
    }
    /**
     * Thumbnail image and portions, like button and openInBrowser button.
     */
    var yieldCount by remember(recipeYield) { mutableStateOf(recipeYield) }
    val alpha: Float by animateFloatAsState(
        targetValue = if (scrollState.value > 600) 0f else 1f,
        animationSpec = tween(800, easing = LinearOutSlowInEasing)
    )
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    Column(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
        Column {
            RecipeTopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenInBrowserClick) {
                        Icon(Icons.Rounded.OpenInNew, null)
                    }
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            if (isFavorite)
                                Icons.Rounded.Favorite
                            else
                                Icons.Rounded.FavoriteBorder,
                            contentDescription = null
                        )

                    }
                }
            )
        }

        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .graphicsLayer(alpha = alpha, translationY = (scrollState.value / 2).toFloat()),
                contentAlignment = Alignment.BottomStart
            ) {
                NetworkImage(
                    url = image,
                    modifier = Modifier.fillMaxSize()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(topEnd = 28.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(bottom = 0.dp, top = 4.dp, start = 12.dp, end = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(21.dp)
                            )
                            IconButton(
                                onClick = { yieldCount-- },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.RemoveCircleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            AnimatedContent(
                                targetState = yieldCount,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        slideInHorizontally { width -> width } + fadeIn() with
                                                slideOutHorizontally { width -> -width } + fadeOut()
                                    } else {
                                        slideInHorizontally { width -> -width } + fadeIn() with
                                                slideOutHorizontally { width -> width }  + fadeOut()
                                    }.using(SizeTransform(clip = false))
                                }
                            ) { targetYield ->
                                Text(
                                    text = "$targetYield",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { yieldCount++ },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(21.dp)
                                )
                            }

                        }
                    }
                }
            }

            Surface() {
                Column() {
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
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            IconButton(onClick = { overflowMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = null
                                )
                            }
                            DropdownMenu(
                                expanded = overflowMenuExpanded,
                                onDismissRequest = { overflowMenuExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                DropdownMenuItem(onClick = navigateToEditScreen) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = null
                                    )
                                    RecipeSpacer()
                                    Text(text = "Redigera")
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        openAlertDialog.value = true
                                        overflowMenuExpanded = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = null
                                    )
                                    RecipeSpacer()
                                    Text(text = "Radera")
                                }
                            }
                        }

                    }

                    /**
                     * Time and categories.
                     */
                    val tagStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                        background = MaterialTheme.colorScheme.tertiaryContainer,
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
                                },
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    RecipeSpacer()

                    /**
                     * Recipe description.
                     */
                    Text(
                        text = description,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    RecipeSpacer()
                }
            }

            /**
             * Show ingredient list if it's not null.
             */
            if (ingredients != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ingredienser",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ingredients.forEach { ingredient ->
                        val ing = remember(ingredient) {
                            ingredient.formatIngredient()
                        }
                        val calcYield = remember(ing.amount, recipeYield, yieldCount) {
                            ing.amount
                                ?.div(recipeYield)
                                ?.times(yieldCount)
                                .toString().roundToIntOrFloat()
                        }

                        IngredientListCard(
                            ingredient = ing.name,
                            amount = calcYield + " " + ing.unit
                        )
                    }
                }
            }

            /**
             * Show instruction list if it's not null.
             */
            if (instructions != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Instruktioner", style = MaterialTheme.typography.headlineMedium)
                    instructions.forEachIndexed { index, instruction ->
                        InstructionListCard(instruction = instruction)
                    }
                }
            }

            /**
             * Show webview if it's a web recipe (not image recipe).
             */
//            if (isValidUrl(recipeUrl)) {
//                val domainName = getDomainName(recipeUrl!!)
//                ExpandingDetail(title = domainName) {
//                    Row(
//                        modifier = Modifier
//                    ) {
//                        AndroidView(factory = {
//                            WebView(it).apply {
//                                layoutParams = ViewGroup.LayoutParams(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.MATCH_PARENT
//                                )
//                                settings.apply {
//                                    javaScriptEnabled = true
//                                    domStorageEnabled = true
//                                    loadsImagesAutomatically = true
//                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//                                }
//                                webChromeClient = WebChromeClient()
//                                loadUrl(recipeUrl)
//                            }
//                        }, update = {
//                            it.loadUrl(recipeUrl)
//                        })
//                    }
//                }
//                RecipeSpacer()
//            }

            /**
             * Show recipe image if it's not null.
             */
            if (recipeImage != null) {
                ExpandingDetail(title = "Receptbild") {
                    var scale by remember { mutableStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }
                    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
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
            RecipeSpacer()
        }
        



    }
}