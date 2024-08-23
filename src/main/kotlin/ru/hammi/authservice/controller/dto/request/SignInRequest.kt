package ru.hammi.authservice.controller.dto.request

data class SignInRequest(
    val username: String,
    val password: String
)