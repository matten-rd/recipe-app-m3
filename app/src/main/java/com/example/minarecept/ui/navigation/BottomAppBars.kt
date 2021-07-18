package com.example.minarecept.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.minarecept.ui.components.RecipeExtendedFab
import com.example.minarecept.ui.theme.DarkGrey
import com.example.minarecept.ui.theme.LightGrey
import com.example.minarecept.ui.theme.Orange500
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.BottomNavigation

@Composable
fun RecipeBottomNav(navController: NavController, tabs: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route in tabs.map { it.route }) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.surface,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.navigationBars
            )
        ) {

            tabs.forEach { screen ->
                BottomNavigationItem(
                    selectedContentColor = Orange500,
                    unselectedContentColor = if (MaterialTheme.colors.isLight) DarkGrey else LightGrey,
                    icon = { Icon(imageVector = screen.icon!!, contentDescription = null) },
                    label = {
                        Text(
                            stringResource(id = screen.resourceId!!),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExtendedFabBottomAppBar(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = modifier.height(100.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        RecipeExtendedFab(
            text = { Text(text = buttonText) },
            onClick = onClick ,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
        )
    }
}