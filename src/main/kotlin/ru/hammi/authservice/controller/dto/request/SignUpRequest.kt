package ru.hammi.authservice.controller.dto.request

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String
)