package com.strand.minarecept.data.remote.firebase

data class User(
    val userId: String,
    val userName: String? = null,
    val email: String,
    val isEmailVerified: Boolean = false,
    val profilePicture: String? = null
)
