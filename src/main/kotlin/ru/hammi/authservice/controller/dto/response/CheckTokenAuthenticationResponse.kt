package ru.hammi.authservice.controller.dto.response

import org.springframework.security.core.userdetails.UserDetails

class CheckTokenAuthenticationResponse(
    val userDetails: UserDetails? = null,
    val isAuthenticated: Boolean
)