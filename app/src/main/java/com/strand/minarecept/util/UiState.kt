package com.strand.minarecept.util

sealed class UiState {
    object SignedIn : UiState()
    object SignedOut : UiState()
    object Loading : UiState()
    object Error : UiState()
}

sealed class UiResult<out T> {
    object Loading : UiResult<Nothing>()
    data class Success<T>(val data: T?) : UiResult<T>()
    data class Failure<E>(val exception: E?) : UiResult<Nothing>()
}
