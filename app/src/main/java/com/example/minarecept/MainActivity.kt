package com.example.minarecept

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.ui.components.AddNewRecipeBottomSheet
import com.example.minarecept.ui.components.BottomSheetMainScreens
import com.example.minarecept.ui.components.FilterBottomSheet
import com.example.minarecept.ui.components.RecipeFab
import com.example.minarecept.ui.createAndEdit.CreateViewModel
import com.example.minarecept.ui.navigation.*
import com.example.minarecept.ui.theme.MinaReceptTheme
import com.example.minarecept.util.isValidUrl
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val createViewModel: CreateViewModel by viewModels()

    @ExperimentalCoilApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action.equals(Intent.ACTION_SEND)) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (isValidUrl(text)) {
                createViewModel.createUrlRecipe(text!!)
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
            }

            MinaReceptTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    RecipesApp()
                }
            }
        }
    }
}


@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun RecipesApp() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var selectedBottomSheet by remember { mutableStateOf(BottomSheetMainScreens.ADD) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val openSheet: (BottomSheetMainScreens) -> Unit = {
        selectedBottomSheet = it
        scope.launch { sheetState.show() }
    }
    val closeSheet: () -> Unit = {
        scope.launch { sheetState.hide() }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (selectedBottomSheet) {
                BottomSheetMainScreens.ADD -> AddNewRecipeBottomSheet(
                    navigateToNewUrlRecipe = {
                        navController.navigate(Screen.CreateUrl.route)
                        closeSheet()
                    },
                    navigateToNewImageRecipe = {
                        navController.navigate(Screen.CreateImage.route)
                        closeSheet()
                    },
                    navigateToPhotoActivity = {

                    }
                )
                BottomSheetMainScreens.FILTER -> FilterBottomSheet(
                    onSaveClick = { closeSheet() }
                )
            }
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                when (currentDestination?.route) {
                    Screen.Home.route -> RecipeTopAppBar(title = stringResource(id = R.string.screen_home))
                    Screen.SavedRecipes.route -> RecipeTopAppBar(title = stringResource(id = R.string.screen_my_recipes))
                    Screen.Planning.route -> RecipeTopAppBar(title = stringResource(id = R.string.screen_planning))

                    Screen.Detail.route -> RecipeTopAppBar(
                        backgroundColor = MaterialTheme.colors.surface.copy(0.6f),
                        contentColor = MaterialTheme.colors.onSurface
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    }
                    Screen.CreateUrl.route -> RecipeTopAppBar(
                        title = "Spara receptlänk"
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    }
                    Screen.CreateImage.route -> RecipeTopAppBar(
                        title = "Spara bildrecept"
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    }
                    Screen.EditScreen.route -> RecipeTopAppBar(
                        title = "Redigera recept"
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                        }
                    }
                }
            },
            bottomBar = {
                when (currentDestination?.route) {
                    in bottomNavItems.map { it.route } -> RecipeBottomNav(
                        navController = navController,
                        tabs = bottomNavItems
                    )
                    Screen.CreateUrl.route -> {
                        val createViewModel = hiltViewModel<CreateViewModel>()
                        val url by createViewModel.url.observeAsState()
                        ExtendedFabBottomAppBar(
                            buttonText = "Spara",
                            onClick = {
                                if (!url.isNullOrEmpty() && isValidUrl(url)) {
                                    createViewModel.createUrlRecipe(url!!)
                                    navController.navigate(Screen.SavedRecipes.route)
                                }
                            }
                        )
                    }

                }
            },
            floatingActionButton = {
                if (currentDestination?.route == Screen.SavedRecipes.route)
                    RecipeFab(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        onClick = { openSheet(BottomSheetMainScreens.ADD) }
                    )
            }
        ) { innerPadding ->
            RecipeNavGraph(
                startDestination = Screen.Home.route,
                modifier = Modifier,
                navController = navController,
                contentPadding = innerPadding,
                scope = scope,
                scaffoldState = scaffoldState,
                openFilterSheet = { openSheet(BottomSheetMainScreens.FILTER) }
            )
        }
    }
}