package com.strand.minarecept.ui.authentication

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strand.minarecept.R
import com.strand.minarecept.ui.components.ErrorScreen
import com.strand.minarecept.ui.components.LoadingScreen
import com.strand.minarecept.ui.components.RecipeButton
import com.strand.minarecept.ui.components.RecipeSpacer
import com.strand.minarecept.util.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@ExperimentalComposeUiApi
@Composable
fun LoginScreen(
    contentPadding: PaddingValues,
    onSuccessfulLogin: () -> Unit,
    onSignUpAction: () -> Unit,
    authViewModel: AuthenticationViewModel = viewModel(
        factory = AuthenticationViewModelFactory(
            if (Firebase.auth.currentUser != null) UiState.SignedIn else UiState.SignedOut
        )
    )
) {
    val uiState by authViewModel.uiState.observeAsState(initial = UiState.Loading)
    println(uiState)

    when (uiState) {
        UiState.SignedIn -> onSuccessfulLogin()
        UiState.Loading -> LoadingScreen()
        UiState.Error -> ErrorScreen() // TODO: Show error snackbar and show error on textfields
                                // See ComposeDocs/Side-effects/LaunchedEffect for how to do this
        UiState.SignedOut -> LoginScreenContent(contentPadding, onSignUpAction)
    }
}

@ExperimentalComposeUiApi
@Composable
fun LoginScreenContent(
    contentPadding: PaddingValues,
    onSignUpAction: () -> Unit,
    authViewModel: AuthenticationViewModel = viewModel()
) {
    val email by authViewModel.email.observeAsState(initial = "")
    val password by authViewModel.password.observeAsState(initial = "")

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(stringResource(id = R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(LocalContext.current.applicationContext, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                authViewModel.signInWithCredential(credential)
            } catch (e: ApiException) {
                Log.w("FIREBASE", "Google sign in failed", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailAndPasswordFields(
                email = email,
                password = password,
                onEmailChange = { authViewModel.onEmailChange(it) },
                onPasswordChange = { authViewModel.onPasswordChange(it) }
            )
            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = { /*TODO: Send reset email*/ }
            ) {
                Text(text = stringResource(id = R.string.forgot_password))
            }
            RecipeSpacer()
            RecipeButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        authViewModel.onSignIn(email, password)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Login,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = stringResource(id = R.string.login))
            }
            AuthProviders(
                onGoogleAuth = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }
            )
            RecipeSpacer()
        }

        // No account? Register
        Column(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.no_account_yet))
            TextButton(onClick = onSignUpAction) {
                Text(text = stringResource(id = R.string.register))
            }
        }

    }
}