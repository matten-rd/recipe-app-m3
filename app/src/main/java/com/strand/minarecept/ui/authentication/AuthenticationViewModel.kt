package com.strand.minarecept.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.strand.minarecept.util.UiState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel(
    initialUiState: UiState = UiState.SignedOut
) : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    private val _auth = Firebase.auth

    private val _uiState = MutableLiveData<UiState>(
        if (_auth.currentUser != null)
            UiState.SignedIn
        else
            initialUiState
    )
    val uiState: LiveData<UiState> = _uiState

    fun signInWithCredential(authCredential: AuthCredential) {
        _uiState.value = UiState.Loading
        _auth.signInWithCredential(authCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(FIREBASE_TAG, "signInWithCredential:success")
                    _uiState.value = UiState.SignedIn
                } else {
                    Log.e(FIREBASE_TAG, "signInWithCredential:failure", task.exception)
                    _uiState.value = UiState.Error
                }
            }
    }

    fun onCreateUser(email: String, password: String) {
        _uiState.value = UiState.Loading
        _auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(FIREBASE_TAG, "createUserWithEmail:success")
                    _uiState.value = UiState.SignedIn
                } else {
                    Log.e(FIREBASE_TAG, "createUserWithEmail:failure", task.exception)
                    _uiState.value = UiState.Error
                }
            }
    }


    fun onSignIn(email: String, password: String) {
        _uiState.value = UiState.Loading
        _auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(FIREBASE_TAG, "signInWithEmail:success")
                    _uiState.value = UiState.SignedIn
                } else {
                    Log.e(FIREBASE_TAG, "signInWithEmail:failure", task.exception)
                    _uiState.value = UiState.Error
                }
            }
    }

    fun onSignOut() {
        _uiState.value = UiState.SignedOut
        _auth.signOut()
    }

}

class AuthenticationViewModelFactory(private val uiState: UiState): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthenticationViewModel(uiState) as T
    }
}

private const val FIREBASE_TAG = "Firebase"
