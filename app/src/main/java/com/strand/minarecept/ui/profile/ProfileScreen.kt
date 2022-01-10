package com.strand.minarecept.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import com.strand.minarecept.R
import com.strand.minarecept.ui.authentication.AuthenticationViewModel
import com.strand.minarecept.ui.components.*
import com.strand.minarecept.util.UiState

@ExperimentalCoilApi
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    authViewModel: AuthenticationViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.observeAsState(initial = UiState.Loading)
    println(uiState)

    Column(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.SignedIn -> ProfileSignedIn()
            UiState.SignedOut -> onSignOut() // navigates to LoginScreen
            UiState.Loading -> LoadingScreen()
            UiState.Error -> ErrorScreen()
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ProfileSignedIn(
    authViewModel: AuthenticationViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NetworkImage(
                    url = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(150.dp)
                )
                RecipeSpacer()
                Text(text = "Username")
                RecipeSpacer()
                RecipeButton(
                    modifier = Modifier.wrapContentSize(),
                    onClick = { /*TODO: open dialog to change profile*/ }
                ) {
                    Text(text = "Ändra profil")
                }
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ALLMÄNT",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { /*TODO: Open account settings*/ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.account_settings))
                }

                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { /*TODO: Open app settings*/ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.app_settings))
                }

                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { /*TODO: Open contact details*/ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.contact_us))
                }

            }
        }
        // Sign out button
        Column(
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecipeButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { authViewModel.onSignOut() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = stringResource(id = R.string.logout))
            }
        }
    }
}


@Composable
fun ProfileGuest() {
    // TODO: Show dialog to create an account
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Guest")
    }
}