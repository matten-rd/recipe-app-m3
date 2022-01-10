package com.strand.minarecept.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.strand.minarecept.R
import com.strand.minarecept.data.local.FilterPreferences
import com.strand.minarecept.data.local.MealPlan
import com.strand.minarecept.ui.authentication.AuthenticationViewModel
import com.strand.minarecept.ui.authentication.LoginScreen
import com.strand.minarecept.ui.authentication.SignUpScreen
import com.strand.minarecept.ui.components.AddNewRecipeBottomSheet
import com.strand.minarecept.ui.components.BottomSheetAddMealPlan
import com.strand.minarecept.ui.components.FilterBottomSheet
import com.strand.minarecept.ui.components.LoadingScreen
import com.strand.minarecept.ui.createAndEdit.CreateImageScreen
import com.strand.minarecept.ui.createAndEdit.CreateUrlScreen
import com.strand.minarecept.ui.createAndEdit.CreateViewModel
import com.strand.minarecept.ui.createAndEdit.EditScreen
import com.strand.minarecept.ui.detail.DetailScreenState
import com.strand.minarecept.ui.detail.DetailViewModel
import com.strand.minarecept.ui.planning.PlanningScreen
import com.strand.minarecept.ui.planning.PlanningViewModel
import com.strand.minarecept.ui.profile.ProfileScreen
import com.strand.minarecept.ui.saved.SavedScreen
import com.strand.minarecept.ui.saved.SavedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val ANIMATION_DURATION = 400
private val ANIMATION_EASING = LinearOutSlowInEasing
private const val ANIMATION_Y_OFFSET = 2100
private const val ANIMATION_X_OFFSET = 1200
const val RECIPE_ID_KEY = "recipeId"

sealed class Screen(
    val route: String
) {
    object AuthenticationScreens : Screen("authentication")
    object MainScreens : Screen("main")
    object RecipeScreens : Screen("recipe")
}

sealed class AuthenticationSections(
    val route: String
) {
    object Login : AuthenticationSections("authentication/login")
    object SignUp : AuthenticationSections("authentication/signup")
}

sealed class MainSections(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    object SavedRecipes : MainSections("main/saved", R.string.screen_my_recipes, Icons.Rounded.Favorite)
    object Planning : MainSections("main/planning", R.string.screen_planning, Icons.Rounded.DateRange)
    object Groups : MainSections("main/groups", R.string.screen_groups, Icons.Rounded.Groups)
    object Profile : MainSections("main/profile", R.string.screen_profile, Icons.Rounded.AccountCircle)
    object AppSettings : MainSections("main/profile/app_settings", R.string.app_settings, Icons.Rounded.AccountCircle)
    object AccountSettings : MainSections("main/profile/account_settings", R.string.account_settings, Icons.Rounded.AccountCircle)
}
val bottomNavItems = listOf<MainSections>(
    MainSections.SavedRecipes,
    MainSections.Planning,
    MainSections.Groups
)

sealed class RecipeSections(
    val route: String
) {
    object CreateUrl : RecipeSections("recipe/create_url")
    object CreateImage : RecipeSections("recipe/create_image")
    object EditScreen : RecipeSections("recipe/edit")
    object Detail : RecipeSections("recipe/detail")
}

sealed class MainBottomSheets(
    val route: String
) {
    object Add : MainBottomSheets("sheets/add")
    object Filter : MainBottomSheets("sheets/filter")
    object Lunch : MainBottomSheets("sheets/lunch")
    object Dinner : MainBottomSheets("sheets/dinner")
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
fun RecipeNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = Screen.AuthenticationScreens.route,
    contentPadding: PaddingValues,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    scrollBehavior: TopAppBarScrollBehavior,
    createViewModel: CreateViewModel = hiltViewModel(),
    planningViewModel: PlanningViewModel = hiltViewModel(),
    authViewModel: AuthenticationViewModel = viewModel()
) {
    val actions = remember(navController) { MainActions(navController) }
    val floatAnimSpec = remember { tween<Float>(ANIMATION_DURATION, easing = ANIMATION_EASING) }
    val offsetAnimSpec = remember { tween<IntOffset>(ANIMATION_DURATION, easing = ANIMATION_EASING) }
    val intSizeAnimSpec = remember { tween<IntSize>(ANIMATION_DURATION, easing = ANIMATION_EASING) }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        navigation(
            route = Screen.AuthenticationScreens.route,
            startDestination = AuthenticationSections.Login.route
        ) {
            authenticationGraph(
                contentPadding = contentPadding,
                actions = actions,
                authViewModel = authViewModel
            )
        }

        navigation(
            route = Screen.MainScreens.route,
            startDestination = MainSections.SavedRecipes.route,
            enterTransition = { _, target ->
                if (target.destination.route == MainSections.Profile.route) {
                    slideInVertically(
                        initialOffsetY = { ANIMATION_Y_OFFSET },
                        animationSpec = offsetAnimSpec
                    ) + fadeIn(animationSpec = floatAnimSpec)
                } else {
                    fadeIn(animationSpec = floatAnimSpec)
                }
            }
        ) {
            mainGraph(
                contentPadding = contentPadding,
                actions = actions,
                planningViewModel = planningViewModel,
                createViewModel = createViewModel
            )
        }

        navigation(
            route = Screen.RecipeScreens.route,
            startDestination = RecipeSections.CreateUrl.route
        ) {
            recipeGraph(
                contentPadding = contentPadding,
                actions = actions,
                scaffoldState = scaffoldState,
                createViewModel = createViewModel
            )
        }

    }
}

/**
 * Class to simplify all navigation actions used in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToRecipe: (String) -> Unit = { recipeId ->
        navController.navigate("${RecipeSections.Detail.route}/$recipeId")
    }
    val navigateToEdit: (String) -> Unit = { recipeId ->
        navController.navigate("${RecipeSections.EditScreen.route}/$recipeId")
    }
    val navigateToRoute: (String) -> Unit = { route ->
        navController.navigate(route)
    }
    val navigateToRoutePop: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(0) // clears the backstack so you can't use the system backbutton to navigate back
        }
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}


/**
 * Navigation graph to handle all main screens.
 *
 * @param [SavedScreen]
 * @param [PlanningScreen]
 * @param [GroupScreen]
 * @param [ProfileScreen]
 */
@ExperimentalPagerApi
@ExperimentalMaterialNavigationApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
fun NavGraphBuilder.mainGraph(
    contentPadding: PaddingValues,
    actions: MainActions,
    planningViewModel: PlanningViewModel,
    createViewModel: CreateViewModel
) {
    composable(route = MainSections.SavedRecipes.route) {
        val loading by createViewModel.loading.collectAsState()
        if (loading) {
            LoadingScreen()
        } else {
            val savedViewModel = hiltViewModel<SavedViewModel>()
            SavedScreen(
                contentPadding = contentPadding,
                navigateToRecipe = actions.navigateToRecipe,
                openSheet = { actions.navigateToRoute(MainBottomSheets.Filter.route) },
                savedViewModel = savedViewModel
            )
        }
    }
    composable(MainSections.Planning.route) {
        PlanningScreen(
            contentPadding = contentPadding,
            openLunchSheet = { actions.navigateToRoute(MainBottomSheets.Lunch.route) },
            openDinnerSheet = { actions.navigateToRoute(MainBottomSheets.Dinner.route) },
            planningViewModel = planningViewModel
        )
    }
    composable(MainSections.Groups.route) {  }
    composable(MainSections.Profile.route) {
        ProfileScreen(
            modifier = Modifier.padding(contentPadding),
            onSignOut = { actions.navigateToRoutePop(AuthenticationSections.Login.route) }
        )
    }

    bottomSheet(MainBottomSheets.Add.route) {
        AddNewRecipeBottomSheet(
            navigateToNewUrlRecipe = {
                actions.navigateToRoute(RecipeSections.CreateUrl.route)
            },
            navigateToNewImageRecipe = {
                actions.navigateToRoute(RecipeSections.CreateImage.route)
            },
            navigateToPhotoActivity = {

            }
        )
    }
    bottomSheet(MainBottomSheets.Filter.route) {
        val savedViewModel = hiltViewModel<SavedViewModel>()
        val preferences by savedViewModel.preferencesFlow
            .collectAsState(initial = FilterPreferences())

        FilterBottomSheet(
            onSaveClick = { actions.navigateToRoute(MainSections.SavedRecipes.route) },
            preferences,
            savedViewModel
        )
    }
    bottomSheet(MainBottomSheets.Lunch.route) {
        val selectedDate by planningViewModel.selectedDate.collectAsState()
        val currentMealPlan by planningViewModel.loadMealPlanByDate(selectedDate).observeAsState()
        val mealPlanText by planningViewModel.mealPlanText.observeAsState(initial = "")
        planningViewModel.onMealPlanTextChange(currentMealPlan?.lunch ?: "")

        BottomSheetAddMealPlan(
            title = "Lägg till en lunch",
            saveClick = {
                // Inserts the mealPlan with conflictStrategy REPLACE
                // This will automatically delete and insert a new row if a row with the same PK already exists
                // This works because the PK is just the date and not autogenerated
                planningViewModel.insertReplaceMealPlan(
                    MealPlan(
                        date = selectedDate,
                        lunch = if (mealPlanText.isNotBlank()) mealPlanText else null,
                        dinner = currentMealPlan?.dinner
                    )
                )
                actions.navigateToRoute(MainSections.Planning.route)
            },
            planningViewModel = planningViewModel
        )
    }
    bottomSheet(MainBottomSheets.Dinner.route) {
        val selectedDate by planningViewModel.selectedDate.collectAsState()
        val currentMealPlan by planningViewModel.loadMealPlanByDate(selectedDate).observeAsState()
        val mealPlanText by planningViewModel.mealPlanText.observeAsState(initial = "")
        planningViewModel.onMealPlanTextChange(currentMealPlan?.dinner ?: "")

        BottomSheetAddMealPlan(
            title = "Lägg till en middag",
            saveClick = {
                // Inserts the mealPlan with conflictStrategy REPLACE
                // This will automatically delete and insert a new row if a row with the same PK already exists
                // This works because the PK is just the date and not autogenerated
                planningViewModel.insertReplaceMealPlan(
                    MealPlan(
                        date = selectedDate,
                        dinner = if (mealPlanText.isNotBlank()) mealPlanText else null,
                        lunch = currentMealPlan?.lunch
                    )
                )
                actions.navigateToRoute(MainSections.Planning.route)
            },
            planningViewModel = planningViewModel
        )
    }
}

/**
 * Navigation graph to handle all things recipe related.
 *
 * @param [CreateUrlScreen]
 * @param [CreateImageScreen]
 * @param [EditScreen]
 * @param [DetailScreenState]
 */
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
fun NavGraphBuilder.recipeGraph(
    contentPadding: PaddingValues,
    actions: MainActions,
    scaffoldState: ScaffoldState,
    createViewModel: CreateViewModel
) {
    composable(
        RecipeSections.CreateUrl.route
    ) {
        CreateUrlScreen(createViewModel = createViewModel, contentPadding = contentPadding)
    }
    composable(
        RecipeSections.CreateImage.route
    ) {
        CreateImageScreen(
            createViewModel = createViewModel,
            scaffoldState = scaffoldState,
            contentPadding = contentPadding,
            navigateToSavedRecipes = { actions.navigateToRoute(MainSections.SavedRecipes.route) }
        )
    }
    composable(
        "${RecipeSections.EditScreen.route}/{$RECIPE_ID_KEY}",
        arguments = listOf(navArgument(RECIPE_ID_KEY) { type = NavType.StringType })
    ) { navBackStackEntry ->
        val recipeId = navBackStackEntry.arguments?.getString(RECIPE_ID_KEY)
        if (recipeId != null) {
            createViewModel.setRecipeId(recipeId)
            val recipeIdVM by createViewModel.recipeId.collectAsState()
            if (recipeIdVM.isNotBlank())
                EditScreen(
                    recipeId = recipeIdVM,
                    createViewModel = createViewModel,
                    scaffoldState = scaffoldState,
                    contentPadding = contentPadding,
                    navigateToDetailScreen = actions.upPress
                )
            else
                LoadingScreen()
        } else {
            // TODO: Show error screen / error snackbar
        }
    }
    composable(
        "${RecipeSections.Detail.route}/{$RECIPE_ID_KEY}",
        arguments = listOf(navArgument(RECIPE_ID_KEY) { type = NavType.StringType })
    ) { navBackStackEntry ->
        val detailViewModel = hiltViewModel<DetailViewModel>()
        val recipeId = navBackStackEntry.arguments?.getString(RECIPE_ID_KEY)
        if (recipeId != null) {
            detailViewModel.setRecipeId(recipeId)
            val recipeIdVM by detailViewModel.recipeId.collectAsState()
            if (recipeIdVM.isNotBlank())
                DetailScreenState(
                    recipeId = recipeIdVM,
                    navigateUp = actions.upPress,
                    navigateToEditScreen = actions.navigateToEdit,
                    detailViewModel = detailViewModel
                )
            else
                LoadingScreen()
        } else {
            // TODO: Show error screen / error snackbar
        }
    }
}

/**
 * Navigation graph to handle all things authentication related.
 *
 * @param [LoginScreen]
 * @param [SignUpScreen]
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun NavGraphBuilder.authenticationGraph(
    contentPadding: PaddingValues,
    actions: MainActions,
    authViewModel: AuthenticationViewModel
) {
    composable(AuthenticationSections.Login.route) {
        LoginScreen(
            contentPadding = contentPadding,
            onSuccessfulLogin = { actions.navigateToRoutePop(MainSections.SavedRecipes.route) },
            onSignUpAction = { actions.navigateToRoute(AuthenticationSections.SignUp.route) }
        )
    }
    composable(AuthenticationSections.SignUp.route) {
        SignUpScreen(
            contentPadding = contentPadding,
            onSuccessfulSignUp = { actions.navigateToRoutePop(MainSections.SavedRecipes.route) },
            onSignInAction = { actions.navigateToRoute(AuthenticationSections.Login.route) }
        )
    }
}