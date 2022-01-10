package com.strand.minarecept

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.plusAssign
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.strand.minarecept.ui.components.RecipeFab
import com.strand.minarecept.ui.createAndEdit.CreateViewModel
import com.strand.minarecept.ui.navigation.*
import com.strand.minarecept.ui.theme.AppTheme
import com.strand.minarecept.util.isValidUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.collections.contains

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val createViewModel: CreateViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    @ExperimentalMaterial3Api
    @ExperimentalPagerApi
    @ExperimentalMaterialNavigationApi
    @ExperimentalCoilApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalCoroutinesApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle browser share menu
        if (intent.action.equals(Intent.ACTION_SEND)) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (isValidUrl(text)) {
                createViewModel.createUrlRecipe(text!!)
            }
        }
//        Firebase.firestore.clearPersistence().addOnSuccessListener {
//            println("Cache cleared")
//        }

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) {
            Screen.MainScreens.route
        } else {
            Screen.AuthenticationScreens.route
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }

            AppTheme(isDynamicColor = true) {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    CompositionLocalProvider(
                        values = arrayOf(LocalContentColor provides MaterialTheme.colorScheme.onSurface)
                    ) {
                        RecipesApp(startDestination = startDestination)
                    }

                }
            }
        }
    }
}


@ExperimentalMaterial3Api
@ExperimentalPagerApi
@ExperimentalMaterialNavigationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun RecipesApp(
    startDestination: String = Screen.AuthenticationScreens.route
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    val actions = remember(navController) { MainActions(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    com.google.accompanist.navigation.material.ModalBottomSheetLayout(
        bottomSheetNavigator,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column {
                    TopAppBarHandler(
                        currentRoute = currentDestination?.route,
                        actions = actions,
                        scrollBehavior = scrollBehavior
                    )
                }
            },
            bottomBar = {
                when (currentDestination?.route) {
                    in bottomNavItems.map { it.route } -> RecipeBottomNav(navController = navController)

                    RecipeSections.CreateUrl.route -> {
                        val createViewModel = hiltViewModel<CreateViewModel>()
                        val url by createViewModel.url.observeAsState()
                        ExtendedFabBottomAppBar(
                            buttonText = stringResource(id = R.string.save),
                            onClick = {
                                if (!url.isNullOrBlank() && isValidUrl(url)) {
                                    createViewModel.createUrlRecipe(url!!)
                                    actions.navigateToRoute(MainSections.SavedRecipes.route)
                                }
                            }
                        )
                    }

                }
            },
            floatingActionButton = {
                if (currentDestination?.route == MainSections.SavedRecipes.route)
                    RecipeFab(
                        onClick = { actions.navigateToRoute(MainBottomSheets.Add.route) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    }
            }
        ) { innerPadding ->
            RecipeNavGraph(
                navController = navController,
                contentPadding = innerPadding,
                scope = scope,
                scaffoldState = scaffoldState,
                startDestination = startDestination,
                scrollBehavior = scrollBehavior
            )
        }
    }
}