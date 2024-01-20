package com.example.riffrift.Auth

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)