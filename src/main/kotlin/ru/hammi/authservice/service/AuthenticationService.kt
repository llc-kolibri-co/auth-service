package ru.hammi.authservice.service

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hammi.authservice.controller.AuthController
import ru.hammi.authservice.controller.dto.request.SignInRequest
import ru.hammi.authservice.controller.dto.request.SignUpRequest
import ru.hammi.authservice.controller.dto.response.JwtAuthenticationResponse
import ru.hammi.authservice.entity.Role
import ru.hammi.authservice.entity.UsersEntity
import ru.hammi.authservice.feign.UserServiceFeignClient
import ru.hammi.authservice.feign.dto.request.UserInfoRequest
import ru.startup.hammi.util.toJson

@Service
class AuthenticationService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val userServiceFeignClient: UserServiceFeignClient,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {
    var logger = LoggerFactory.getLogger(AuthenticationService::class.java)


    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Transactional
    fun signUp(request: SignUpRequest): JwtAuthenticationResponse {
        val user = UsersEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email,
            role = Role.ROLE_USER
        )

        userService.create(user)
        logger.info("Сохранена сущность пользователя UsersEntity")
        val userInfo = UserInfoRequest(
            userId = user.id,
            username = user.username,
            email = user.email
        )
        userServiceFeignClient.createUserInfo(userInfo)
        logger.info("Успешно отправлен запрос на создание информации по пользователю. Запрос /api/user/info, request = ${userInfo.toJson()}")
        val jwt = jwtService.generateToken(user)
        logger.info("Сгенерирован jwt токен по сущности пользователя UsersEntity")
        return JwtAuthenticationResponse(jwt)
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    fun signIn(request: SignInRequest): JwtAuthenticationResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
            request.username,
            request.password
        )
        )
        logger.info("Произведена аутентификация пользователя")
        val user = userService
            .userDetailsService()
            .loadUserByUsername(request.username)
        logger.info("Получен UserDetails пользователя по username = ${request.username}")
        val jwt = jwtService.generateToken(user)
        logger.info("Сгенерирован jwt токен по сущности пользователя UsersEntity")
        return JwtAuthenticationResponse(jwt)
    }
}