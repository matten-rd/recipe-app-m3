package com.example.minarecept.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import coil.annotation.ExperimentalCoilApi
import com.example.minarecept.R
import com.example.minarecept.ui.components.LoadingScreen
import com.example.minarecept.ui.createAndEdit.CreateImageScreen
import com.example.minarecept.ui.createAndEdit.CreateUrlScreen
import com.example.minarecept.ui.createAndEdit.CreateViewModel
import com.example.minarecept.ui.createAndEdit.EditScreen
import com.example.minarecept.ui.detail.DetailScreenState
import com.example.minarecept.ui.detail.DetailViewModel
import com.example.minarecept.ui.home.HomeScreen
import com.example.minarecept.ui.home.HomeViewModel
import com.example.minarecept.ui.planning.PlanningScreen
import com.example.minarecept.ui.saved.SavedScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi


sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int? = null,
    val icon: ImageVector? = null
) {
    object Home : Screen("home", R.string.screen_home, Icons.Default.Home)
    object SavedRecipes : Screen("saved", R.string.screen_my_recipes, Icons.Default.Favorite)
    object Planning : Screen("planning", R.string.screen_planning, Icons.Default.DateRange)

    object CreateUrl : Screen("create_url")
    object CreateImage : Screen("create_image")
    object EditScreen : Screen("edit/{recipeId}")

    object Detail : Screen("detail/{recipeId}")
}

val bottomNavItems = listOf<Screen>(
    Screen.Home,
    Screen.SavedRecipes,
    Screen.Planning
)

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun RecipeNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    contentPadding: PaddingValues,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    openFilterSheet: () -> Unit,
    createViewModel: CreateViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen(contentPadding) }
        composable(Screen.SavedRecipes.route) {
            val loading by createViewModel.loading.collectAsState()
            if (loading) {
                LoadingScreen()
            } else {
                val homeViewModel = hiltViewModel<HomeViewModel>()
                SavedScreen(contentPadding, navController, openFilterSheet, homeViewModel)
            }
        }
        composable(Screen.Planning.route) { PlanningScreen(contentPadding) }

        composable(Screen.CreateUrl.route) {
            CreateUrlScreen(createViewModel = createViewModel, contentPadding = contentPadding)
        }
        composable(Screen.CreateImage.route) {
            CreateImageScreen(
                createViewModel = createViewModel,
                scaffoldState = scaffoldState,
                contentPadding = contentPadding,
                navigateToSavedRecipes = { navController.navigate(Screen.SavedRecipes.route) }
            )
        }
        composable(
            Screen.EditScreen.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { navBackStackEntry ->
            val recipeId = navBackStackEntry.arguments?.getInt("recipeId")
            EditScreen(
                recipeId = recipeId,
                createViewModel = createViewModel,
                scaffoldState = scaffoldState,
                contentPadding = contentPadding,
                navigateToDetailScreen = { navController.navigateUp() }
            )
        }
        composable(
            Screen.Detail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) {  navBackStackEntry ->
            val detailViewModel = hiltViewModel<DetailViewModel>()
            val recipeId = navBackStackEntry.arguments?.getInt("recipeId")
            DetailScreenState(
                recipeId = recipeId,
                navigateUp = { navController.navigateUp() },
                navigateToEditScreen = { navController.navigate("edit/$recipeId") },
                scope = scope,
                scaffoldState = scaffoldState,
                detailViewModel = detailViewModel
            )
        }
    }
}