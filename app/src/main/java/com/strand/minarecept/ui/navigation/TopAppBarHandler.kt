package com.strand.minarecept.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.strand.minarecept.R

@ExperimentalMaterial3Api
@Composable
fun TopAppBarHandler(
    currentRoute: String?,
    actions: MainActions,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val resId = remember(currentRoute) { getResFromRoute(currentRoute) }
    val title = stringResource(id = resId)

    when (currentRoute) {
        // BottomNav + BottomSheets
        in (bottomNavItems.map { it.route } + bottomSheets.map { it.route }) -> RecipeTopAppBar(
            scrollBehavior = scrollBehavior,
            title = title,
            actions = {
                IconButton(
                    onClick = { actions.navigateToRoute(MainSections.Profile.route) }
                ) {
                    Icon(MainSections.Profile.icon, null)
                }
            },
            containerColor = if (title == "Planera")
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surface

        )
        // Profile
        MainSections.Profile.route -> NavigationTopAppBar(
            title = title,
            onNavigatePress = actions.upPress,
            imageVector = Icons.Rounded.Close
        )

        // Login
        AuthenticationSections.Login.route -> RecipeTopAppBar(title = title)
        // Detail
        "${RecipeSections.Detail.route}/{$RECIPE_ID_KEY}" -> {}
        // All other screen
        else -> NavigationTopAppBar(
            scrollBehavior = scrollBehavior,
            title = title,
            onNavigatePress = actions.upPress
        )
    }
}

private fun getResFromRoute(route: String?): Int {
    return when (route) {
        // Main
        MainSections.SavedRecipes.route -> MainSections.SavedRecipes.resourceId
        MainSections.Planning.route -> MainSections.Planning.resourceId
        MainSections.Groups.route -> MainSections.Groups.resourceId
        MainSections.Profile.route -> MainSections.Profile.resourceId
        MainSections.AppSettings.route -> MainSections.AppSettings.resourceId
        MainSections.AccountSettings.route -> MainSections.AccountSettings.resourceId

        // Auth
        AuthenticationSections.Login.route -> R.string.login
        AuthenticationSections.SignUp.route -> R.string.register

        // Recipe
        RecipeSections.CreateUrl.route -> R.string.create_url_recipe
        RecipeSections.CreateImage.route -> R.string.create_image_recipe
        "${RecipeSections.EditScreen.route}/{$RECIPE_ID_KEY}" -> R.string.edit_recipe

        // BottomSheet
        MainBottomSheets.Add.route -> MainSections.SavedRecipes.resourceId
        MainBottomSheets.Filter.route -> MainSections.SavedRecipes.resourceId
        MainBottomSheets.Lunch.route -> MainSections.Planning.resourceId
        MainBottomSheets.Dinner.route -> MainSections.Planning.resourceId

        else -> R.string.empty_string
    }
}

private val bottomSheets = listOf<MainBottomSheets>(
    MainBottomSheets.Add,
    MainBottomSheets.Filter,
    MainBottomSheets.Lunch,
    MainBottomSheets.Dinner
)