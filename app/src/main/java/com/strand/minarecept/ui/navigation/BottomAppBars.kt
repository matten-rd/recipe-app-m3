package com.strand.minarecept.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun RecipeBottomNav(navController: NavController, tabs: List<MainSections> = bottomNavItems) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route in tabs.map { it.route }) {
        NavigationBar() {
            tabs.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                    label = {
                        Text(
                            stringResource(id = screen.resourceId),
                            maxLines = 1,
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
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.height(100.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .height(50.dp)
        ) {
            Text(text = buttonText)
        }
    }
}