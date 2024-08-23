package ru.hammi.authservice.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.hammi.authservice.controller.dto.request.SignInRequest
import ru.hammi.authservice.controller.dto.request.SignUpRequest
import ru.hammi.authservice.controller.dto.response.JwtAuthenticationResponse
import ru.hammi.authservice.entity.Role
import ru.hammi.authservice.entity.UsersEntity
import ru.hammi.authservice.feign.UserServiceFeignClient
import ru.hammi.authservice.feign.dto.request.UserInfoRequest

@Service
class AuthenticationService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val userServiceFeignClient: UserServiceFeignClient,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {

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
        val userInfo = UserInfoRequest(
            userId = user.id,
            username = user.username,
            email = user.email
        )
        userServiceFeignClient.createUserInfo(userInfo)

        val jwt = jwtService.generateToken(user)
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
        val user = userService
            .userDetailsService()
            .loadUserByUsername(request.username)
        val jwt = jwtService.generateToken(user)

        return JwtAuthenticationResponse(jwt)
    }
}