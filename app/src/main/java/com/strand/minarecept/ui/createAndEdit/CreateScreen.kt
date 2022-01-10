package com.strand.minarecept.ui.createAndEdit

import android.content.Intent
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import com.strand.minarecept.R
import com.strand.minarecept.data.local.FirebaseRecipe
import com.strand.minarecept.data.local.RecipeState
import com.strand.minarecept.data.local.categoriesOptions
import com.strand.minarecept.ui.components.*
import com.strand.minarecept.ui.navigation.ExtendedFabBottomAppBar
import com.strand.minarecept.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun EditScreen(
    recipeId: String,
    createViewModel: CreateViewModel,
    scaffoldState: ScaffoldState,
    contentPadding: PaddingValues,
    navigateToDetailScreen: () -> Unit
) {
    val firebaseRecipe by createViewModel.editrecipe.collectAsState()
    when (firebaseRecipe) {
        is UiResult.Success<FirebaseRecipe> -> {
            LaunchedEffect(Unit) {
                (firebaseRecipe as UiResult.Success<FirebaseRecipe>).data?.let {
                    createViewModel.onRecipeChange(it)
                }
            }
            (firebaseRecipe as UiResult.Success<FirebaseRecipe>).data?.let {
                CreateAndEditScreen(
                    initialRecipe = it,
                    createViewModel = createViewModel,
                    scaffoldState = scaffoldState,
                    contentPadding = contentPadding,
                    navigateBack = navigateToDetailScreen,
                    isUpdate = true
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

@ExperimentalMaterial3Api
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun CreateImageScreen(
    createViewModel: CreateViewModel,
    scaffoldState: ScaffoldState,
    contentPadding: PaddingValues,
    navigateToSavedRecipes: () -> Unit
) {
    Log.d("CreateAndEditScreen", "Main screen recomposed")
    val recipe = remember {
        val uid = UUID.randomUUID().toString()
        FirebaseRecipe(
            recipeId = uid,
            recipeState = RecipeState.IMAGE,
            title = "",
            thumbnailImage = "",
            published = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            lastUpdated = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
    LaunchedEffect(Unit) {
        createViewModel.onRecipeChange(recipe)
    }

    CreateAndEditScreen(
        initialRecipe = recipe,
        createViewModel = createViewModel,
        scaffoldState = scaffoldState,
        contentPadding = contentPadding,
        navigateBack = navigateToSavedRecipes,
        isUpdate = false
    )
}


@ExperimentalMaterial3Api
@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun CreateAndEditScreen(
    initialRecipe: FirebaseRecipe,
    createViewModel: CreateViewModel,
    scaffoldState: ScaffoldState,
    contentPadding: PaddingValues,
    navigateBack: () -> Unit,
    isUpdate: Boolean
) {
    var selectedBottomSheet by remember { mutableStateOf(BottomSheetCreateScreens.PORTIONS) }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = bottomSheetAnimationSpec
    )
    val scope = rememberCoroutineScope()

    val updatingRecipe by createViewModel.recipe.observeAsState(initial = initialRecipe)

    // Ingredients state
    val ingredients by remember {
        mutableStateOf(updatingRecipe.ingredients?.toMutableStateList() ?: mutableStateListOf())
    }
    var newIngredient by remember { mutableStateOf("") }
    // Ingredients state
    val instructions by remember {
        mutableStateOf(updatingRecipe.instructions?.toMutableStateList() ?: mutableStateListOf())
    }
    var newInstruction by remember { mutableStateOf("") }

    val launcherThumbnailImage = rememberOpenDocumentActivityResult(
        initialUri = updatingRecipe.thumbnailImage.toUri(),
        onSuccess = { task ->
            createViewModel.onRecipeChange(updatingRecipe.copy(thumbnailImage = task.result.toString()))
        },
        onError = {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Oops! Något gick fel."
                )
            }
        }
    )
    val launcherRecipeImage = rememberOpenDocumentActivityResult(
        initialUri = updatingRecipe.recipeImage?.toUri(),
        onSuccess = { task ->
            createViewModel.onRecipeChange(updatingRecipe.copy(recipeImage = task.result.toString()))
        },
        onError = {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Oops! Något gick fel."
                )
            }
        }
    )

    val openSheet: (BottomSheetCreateScreens) -> Unit = {
        selectedBottomSheet = it
        scope.launch { sheetState.animateTo(ModalBottomSheetValue.Expanded) }
    }
    val closeSheet: () -> Unit = {
        scope.launch { sheetState.hide() }
    }

    val onShowIngredientSnackBar: (Int, String) -> Unit = { idx, s ->
        scope.launch {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = "Ingrediens borttagen.",
                actionLabel = "ÅNGRA",
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> Log.d("snackBarResult", "Dismissed")
                SnackbarResult.ActionPerformed -> {
                    ingredients.add(idx, s)
                    createViewModel.onRecipeChange(updatingRecipe.copy(ingredients = ingredients))
                }
            }
        }
    }
    val onShowInstructionSnackBar: (Int, String) -> Unit = { idx, s ->
        scope.launch {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = "Instruktionssteg borttaget.",
                actionLabel = "ÅNGRA",
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> Log.d("snackBarResult", "Dismissed")
                SnackbarResult.ActionPerformed -> {
                    instructions.add(idx, s)
                    createViewModel.onRecipeChange(updatingRecipe.copy(instructions = instructions))
                }
            }
        }
    }
    val answer = remember(updatingRecipe.category) {
        updatingRecipe.category?.let { Answer.MultipleChoice(it) }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContent = { 
            when (selectedBottomSheet) {
                BottomSheetCreateScreens.CATEGORY -> BottomSheetCategory(
                    possibleAnswer = categoriesOptions,
                    answer = answer,
                    onAnswerSelected = { newAnswer, selected ->
                        if (answer == null) {
                            createViewModel.onRecipeChange(updatingRecipe.copy(
                                category = listOf(newAnswer)
                            ))
                        } else {
                            createViewModel.onRecipeChange(updatingRecipe.copy(
                                category = answer
                                    .withAnswerSelected(newAnswer, selected)
                                    .answersStringSet
                            ))
                        }
                    },
                    closeSheet = closeSheet
                )
                BottomSheetCreateScreens.PORTIONS -> BottomSheetPortions(
                    range = 0..20,
                    selected = updatingRecipe.yield ?: 0,
                    setSelected = { createViewModel.onRecipeChange(updatingRecipe.copy(yield = it)) },
                    closeSheet = closeSheet
                )
                BottomSheetCreateScreens.TIME -> BottomSheetTime(
                    hourRange = 0..10,
                    selectedHour = getHoursFromDuration(updatingRecipe.totalTime),
                    setSelectedHour = {
                        val min = getMinutesFromDuration(updatingRecipe.totalTime)
                        val newTotalTime = getDurationFromHourAndMinutes(it, min)
                        createViewModel.onRecipeChange(updatingRecipe.copy(totalTime = newTotalTime?.toString()))
                    },
                    minutesRange = 0..59,
                    selectedMinute = getMinutesFromDuration(updatingRecipe.totalTime),
                    setSelectedMinute = {
                        val hour = getHoursFromDuration(updatingRecipe.totalTime)
                        val newTotalTime = getDurationFromHourAndMinutes(hour, it)
                        createViewModel.onRecipeChange(updatingRecipe.copy(totalTime = newTotalTime?.toString()))
                    },
                    closeSheet = closeSheet
                )
            }
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(contentPadding)
                .padding(top = 56.dp)
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            val (content, bottomBar) = createRefs()
            val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

            /**
             * Content
             */
            Column(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(16.dp)
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(bottomBar.top, margin = 56.dp)
                    }
            ) {
                CreateAndEditContent(
                    openSheet = openSheet,
                    recipeState = updatingRecipe.recipeState,
                    title = updatingRecipe.title,
                    setTitle = { createViewModel.onRecipeChange(updatingRecipe.copy(title = it)) },
                    thumbnailImage = launcherThumbnailImage.uri.toString(),
                    setThumbnailImage = { launcherThumbnailImage.launch(arrayOf("image/*")) },
                    recipeImage = launcherRecipeImage.uri.toString(),
                    setRecipeImage = { launcherRecipeImage.launch(arrayOf("image/*")) },
                    description = updatingRecipe.description,
                    setDescription = {
                        createViewModel.onRecipeChange(updatingRecipe.copy(description = it))
                    },
                    ingredients = updatingRecipe.ingredients,
                    onIngredientChange = { old, new ->
                        val idx = ingredients.indexOf(old)
                        ingredients[idx] = new
                        createViewModel.onRecipeChange(updatingRecipe.copy(ingredients = ingredients))
                    },
                    addIngredient = {
                        ingredients.add(newIngredient)
                        createViewModel.onRecipeChange(updatingRecipe.copy(ingredients = ingredients))
                        newIngredient = ""
                    },
                    deleteIngredient = { index ->
                        ingredients.removeAt(index)
                        createViewModel.onRecipeChange(updatingRecipe.copy(ingredients = ingredients))
                    },
                    newIngredient = newIngredient,
                    setNewIngredient = { newIngredient = it },
                    onShowIngredientSnackbar = { idx, str ->
                        onShowIngredientSnackBar(idx, str)
                    },
                    instructions = updatingRecipe.instructions,
                    onInstructionChange = { old, new ->
                        val idx = instructions.indexOf(old)
                        instructions[idx] = new
                        createViewModel.onRecipeChange(updatingRecipe.copy(instructions = instructions))
                    },
                    addInstruction = {
                        instructions.add(newInstruction)
                        createViewModel.onRecipeChange(updatingRecipe.copy(instructions = instructions))
                        newInstruction = ""
                    },
                    deleteInstruction = { index ->
                        instructions.removeAt(index)
                        createViewModel.onRecipeChange(updatingRecipe.copy(instructions = instructions))
                    },
                    newInstruction = newInstruction,
                    setNewInstruction = { newInstruction = it },
                    onShowInstructionSnackbar = { idx, str ->
                        onShowInstructionSnackBar(idx, str)
                    },
                    portions = updatingRecipe.yield ?: 0,
                    time = humanReadableDuration(updatingRecipe.totalTime),
                    numberOfSelectedCategories = updatingRecipe.category?.size ?: 0
                )

            }

            /**
             * Bottom bar.
             */
            val contentResolver = LocalContext.current.applicationContext.contentResolver
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            ExtendedFabBottomAppBar(
                modifier = Modifier
                    .constrainAs(bottomBar) {
                        bottom.linkTo(parent.bottom)
                },
                buttonText = "Spara",
                onClick = {
                    if (updatingRecipe.ingredients.isNullOrEmpty())
                        createViewModel.onRecipeChange(updatingRecipe.copy(ingredients = null))
                    if (updatingRecipe.instructions.isNullOrEmpty())
                        createViewModel.onRecipeChange(updatingRecipe.copy(instructions = null))
                    try {
                        updatingRecipe.recipeImage?.let {
                            contentResolver.takePersistableUriPermission(
                                it.toUri(), takeFlags
                            )
                        }
                        contentResolver.takePersistableUriPermission(
                            updatingRecipe.thumbnailImage.toUri(), takeFlags
                        )
                    } catch (e: Exception) {
                        Log.e("Persistable Uri permission", e.message.toString())
                    }
                    if (isUpdate) {
                        createViewModel.addRecipeToFirebase(
                            updatingRecipe.copy(lastUpdated = OffsetDateTime.now()
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        )
                    } else {
                        createViewModel.addRecipeToFirebase(updatingRecipe)
                    }
                    navigateBack()
                }
            )

        }
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun CreateAndEditContent(
    openSheet: (BottomSheetCreateScreens) -> Unit,
    recipeState: RecipeState,
    title: String,
    setTitle: (String) -> Unit,
    thumbnailImage: String,
    setThumbnailImage: () -> Unit,
    recipeImage: String?,
    setRecipeImage: () -> Unit,
    description: String?,
    setDescription: (String) -> Unit,
    ingredients: List<String>?,
    onIngredientChange: (String, String) -> Unit,
    addIngredient: () -> Unit,
    deleteIngredient: (Int) -> Unit,
    newIngredient: String,
    setNewIngredient: (String) -> Unit,
    onShowIngredientSnackbar: (Int, String) -> Unit,
    instructions: List<String>?,
    onInstructionChange: (String, String) -> Unit,
    addInstruction: () -> Unit,
    deleteInstruction: (Int) -> Unit,
    newInstruction: String,
    setNewInstruction: (String) -> Unit,
    onShowInstructionSnackbar: (Int, String) -> Unit,
    portions: Int,
    time: String,
    numberOfSelectedCategories: Int
) {

    TextFieldWithHeader(
        value = title, onValueChange = setTitle,
        label = "Titel", header = "Titel"
    )
    RecipeSpacer()
    EditImage(url = thumbnailImage, setImage = setThumbnailImage, header = "Miniatyrbild")
    RecipeSpacer()
    if (recipeState == RecipeState.IMAGE) {
        EditImage(url = recipeImage, setImage = setRecipeImage, header = "Receptbild")
        RecipeSpacer()
    }
    TextFieldWithHeader(
        value = description, onValueChange = setDescription,
        label = "Beskrivning", header = "Beskrivning"
    )
    RecipeSpacer()
    EditableList(
        header = "Ingredienser",
        label = "Ingrediens",
        list = ingredients,
        onValueChange = onIngredientChange,
        addToList = addIngredient,
        deleteFromList = deleteIngredient,
        newValue = newIngredient,
        setNewValue = setNewIngredient,
        onShowSnackbar = onShowIngredientSnackbar,
        listItems = { Text(text = it, modifier = Modifier.padding(vertical = 6.dp)) }
    )
    RecipeSpacer()
    EditableList(
        header = "Instruktioner",
        label = "Instruktion",
        list = instructions,
        onValueChange = onInstructionChange,
        addToList = addInstruction,
        deleteFromList = deleteInstruction,
        newValue = newInstruction,
        setNewValue = setNewInstruction,
        onShowSnackbar = onShowInstructionSnackbar,
        listHeaders = {
            Text(
                text = "Steg ${it + 1}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        },
        listItems = { Text(text = it, modifier = Modifier.padding(vertical = 6.dp)) }
    )
    RecipeSpacer()
    TextAndButton(
        text = getQuantityStringZero(
            resId = R.plurals.number_of_portions,
            zeroResId = R.string.number_of_portions_zero,
            quantity = portions
        ),
        onClick = { openSheet(BottomSheetCreateScreens.PORTIONS) }
    )
    RecipeSpacer()
    TextAndButton(text = if (time.isNotEmpty()) time else "Ingen tid vald", onClick = { openSheet(BottomSheetCreateScreens.TIME) })
    RecipeSpacer()
    TextAndButton(
        text = getQuantityStringZero(
            resId = R.plurals.number_of_categories,
            zeroResId = R.string.number_of_categories_zero,
            quantity = numberOfSelectedCategories
        ),
        onClick = { openSheet(BottomSheetCreateScreens.CATEGORY) }
    )
    RecipeSpacer()
}




