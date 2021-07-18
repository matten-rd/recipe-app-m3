package com.example.minarecept.ui.createAndEdit

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.R
import com.example.minarecept.data.local.Recipe
import com.example.minarecept.data.local.RecipeState
import com.example.minarecept.ui.components.*
import com.example.minarecept.ui.navigation.ExtendedFabBottomAppBar
import com.example.minarecept.util.*
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

/**
 * Create screen for web based recipes.
 */
@ExperimentalComposeUiApi
@Composable
fun CreateUrlScreen(
    createViewModel: CreateViewModel,
    contentPadding: PaddingValues
) {
    val url by createViewModel.url.observeAsState("")
    CreateUrlContent(
        url = url,
        onUrlChange = { createViewModel.onUrlChange(it) },
        contentPadding = contentPadding
    )
}

@ExperimentalComposeUiApi
@Composable
fun CreateUrlContent(
    url: String,
    onUrlChange: (String) -> Unit,
    contentPadding: PaddingValues
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .padding(contentPadding)
    ) {
        Text(text = "Ange länken till receptet du vill spara")
        RecipeTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "http://",
            label = "Länk"
        )
        // TODO: Show image of how to share via the browser (like in Whisk).
    }
}

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun EditScreen(
    recipeId: Int?,
    createViewModel: CreateViewModel,
    scaffoldState: ScaffoldState,
    contentPadding: PaddingValues,
    navigateToDetailScreen: () -> Unit
) {
    if (recipeId != null) {
        val recipe by createViewModel.loadCurrentRecipe(recipeId).observeAsState()
        if (recipe != null) {
            createViewModel.onRecipeChange(recipe!!)
            CreateAndEditScreen(
                initialRecipe = recipe!!,
                createViewModel = createViewModel,
                scaffoldState = scaffoldState,
                contentPadding = contentPadding,
                navigateBack = navigateToDetailScreen,
                isUpdate = true
            )
        } else {
            LoadingScreen()
        }
    } else {
        // TODO: show error screen
    }
}

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
    val recipe = Recipe(
        recipeState = RecipeState.IMAGE,
        title = "",
        thumbnailImage = "",
        published = OffsetDateTime.now(),
        lastUpdated = OffsetDateTime.now()
    )
    createViewModel.onRecipeChange(recipe)

    CreateAndEditScreen(
        initialRecipe = recipe,
        createViewModel = createViewModel,
        scaffoldState = scaffoldState,
        contentPadding = contentPadding,
        navigateBack = navigateToSavedRecipes,
        isUpdate = false
    )

}


@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun CreateAndEditScreen(
    initialRecipe: Recipe,
    createViewModel: CreateViewModel,
    scaffoldState: ScaffoldState,
    contentPadding: PaddingValues,
    navigateBack: () -> Unit,
    isUpdate: Boolean
) {
    var selectedBottomSheet by remember { mutableStateOf(BottomSheetCreateScreens.PORTIONS) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val updatingRecipe by createViewModel.recipe.observeAsState(initial = initialRecipe)

    // Image state
    var thumbnailImage by remember { mutableStateOf(updatingRecipe.thumbnailImage) }
    var recipeImage by remember { mutableStateOf(updatingRecipe.recipeImage) }
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

    val launcherThumbnailImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            thumbnailImage = uri.toString()
            createViewModel.onRecipeChange(updatingRecipe.copy(thumbnailImage = thumbnailImage))
        }
    }
    val launcherRecipeImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            recipeImage = uri.toString()
            createViewModel.onRecipeChange(updatingRecipe.copy(recipeImage = recipeImage))
        }
    }

    val openSheet: (BottomSheetCreateScreens) -> Unit = {
        selectedBottomSheet = it
        scope.launch { sheetState.show() }
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
    val answer = updatingRecipe.category?.let { Answer.MultipleChoice(it) }

    val categoryOptions = getCategoryOptions()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { 
            when (selectedBottomSheet) {
                BottomSheetCreateScreens.CATEGORY -> BottomSheetCategory(
                    possibleAnswer = PossibleAnswer.MultipleChoice(categoryOptions),
                    answer = answer,
                    onAnswerSelected = { newAnswer, selected ->
                        if (answer == null) {
                            createViewModel.onRecipeChange(updatingRecipe.copy(category = setOf(newAnswer)))
                        } else {
                            createViewModel.onRecipeChange(updatingRecipe.copy(
                                category = answer.withAnswerSelected(newAnswer, selected).answersStringSet
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
                        createViewModel.onRecipeChange(updatingRecipe.copy(totalTime = newTotalTime))
                    },
                    minutesRange = 0..59,
                    selectedMinute = getMinutesFromDuration(updatingRecipe.totalTime),
                    setSelectedMinute = {
                        val hour = getHoursFromDuration(updatingRecipe.totalTime)
                        val newTotalTime = getDurationFromHourAndMinutes(hour, it)
                        createViewModel.onRecipeChange(updatingRecipe.copy(totalTime = newTotalTime))
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

            /**
             * Content
             */
            Column(
                modifier = Modifier
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
                    thumbnailImage = updatingRecipe.thumbnailImage,
                    setThumbnailImage = { launcherThumbnailImage.launch(arrayOf("image/*")) },
                    recipeImage = updatingRecipe.recipeImage,
                    setRecipeImage = { launcherRecipeImage.launch(arrayOf("image/*")) },
                    description = updatingRecipe.description,
                    setDescription = {
                        createViewModel.onRecipeChange(
                            updatingRecipe.copy(
                                description = it
                            )
                        )
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
                        contentResolver.takePersistableUriPermission(
                            Uri.parse(updatingRecipe.recipeImage), takeFlags
                        )
                        contentResolver.takePersistableUriPermission(
                            Uri.parse(updatingRecipe.thumbnailImage), takeFlags
                        )
                    } catch (e: Exception) {
                        Log.e("Persistable Uri permission", e.message.toString())
                    }
                    if (isUpdate) {
                        createViewModel.updateRecipe(updatingRecipe.copy(lastUpdated = OffsetDateTime.now()))
                    } else {
                        createViewModel.insertRecipe(updatingRecipe)
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




