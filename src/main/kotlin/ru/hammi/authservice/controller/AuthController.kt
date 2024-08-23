package ru.hammi.authservice.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.hammi.authservice.controller.dto.request.SignInRequest
import ru.hammi.authservice.controller.dto.request.SignUpRequest
import ru.hammi.authservice.controller.dto.response.CheckTokenAuthenticationResponse
import ru.hammi.authservice.controller.dto.response.JwtAuthenticationResponse
import ru.hammi.authservice.service.AuthenticationService
import ru.hammi.authservice.service.JwtService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val jwtService: JwtService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignUpRequest): JwtAuthenticationResponse {
        return authenticationService.signUp(request)
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: SignInRequest): JwtAuthenticationResponse {
        return authenticationService.signIn(request)
    }

    @PostMapping("/token/validate")
    fun isTokenValid(@RequestBody token: String): CheckTokenAuthenticationResponse {
        return jwtService.validateToken(token)
    }
}