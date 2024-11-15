package ru.hammi.authservice.controller

import org.slf4j.LoggerFactory
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
import ru.startup.hammi.util.toJson

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val jwtService: JwtService
) {
    var logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignUpRequest): JwtAuthenticationResponse {
        logger.info("Получен запрос на регистрацию пользователя (POST /auth/sign-up), request = ${request.toJson()}")
        val response = authenticationService.signUp(request)
        logger.info("Успешно обработан запрос (POST /auth/sign-up)")
        return response
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: SignInRequest): JwtAuthenticationResponse {
        logger.info("Получен запрос на аутентификацию пользователя (POST /auth/sign-in), request = ${request.toJson()}")
        val response = authenticationService.signIn(request)
        logger.info("Успешно обработан запрос (POST /auth/sign-in)")
        return response
    }

    @PostMapping("/token/validate")
    fun isTokenValid(@RequestBody token: String): CheckTokenAuthenticationResponse {
        logger.info("Получен запрос на проверку валидности токена пользователя (POST /auth/token/validate), request = ${token}")
        val response = jwtService.validateToken(token)
        logger.info("Успешно обработан запрос (POST /auth/token/validate)")
        return response
    }
}