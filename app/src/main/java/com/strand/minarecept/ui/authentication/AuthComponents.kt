package com.strand.minarecept.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.strand.minarecept.R
import com.strand.minarecept.ui.components.RecipeButton
import com.strand.minarecept.ui.components.RecipeSpacer
import com.strand.minarecept.ui.components.RecipeTextField


@ExperimentalComposeUiApi
@Composable
fun EmailAndPasswordFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    var passwordVisibility by remember { mutableStateOf(false) }

    RecipeTextField(
        value = email,
        onValueChange = onEmailChange,
        label = stringResource(id = R.string.email),
        placeholder = stringResource(id = R.string.email_example),
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Email
    )
    RecipeSpacer()
    RecipeTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = stringResource(id = R.string.password),
        placeholder = stringResource(id = R.string.password),
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon =
                if (passwordVisibility) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
    )
}

@Composable
fun AuthProviders(
    onGoogleAuth: () -> Unit
) {
    RecipeSpacer()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(modifier = Modifier.width(56.dp).background(MaterialTheme.colorScheme.onSurface))
        RecipeSpacer()
        Text(text = stringResource(id = R.string.or))
        RecipeSpacer()
        Divider(modifier = Modifier.width(56.dp).background(MaterialTheme.colorScheme.onSurface))
    }
    RecipeSpacer()

    // Google
    RecipeButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onGoogleAuth
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_icon),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = stringResource(id = R.string.google_auth),
        )
    }
}