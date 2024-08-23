package ru.hammi.authservice.configuration

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import ru.hammi.authservice.service.JwtService
import ru.hammi.authservice.service.UserService

@Service
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userService: UserService
) : OncePerRequestFilter() {

    companion object {
        const val BEARER_PREFIX = "Bearer "
        const val HEADER_NAME = "Authorization"
    }
    val BEARER_PREFIX = "Bearer "
    val HEADER_NAME = "Authorization"



    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader = request.getHeader(HEADER_NAME)

        if (authHeader.isNullOrEmpty() || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(JwtAuthenticationFilter.BEARER_PREFIX.length)
        val username = jwtService.extractUserName(jwt)

        if (username.isNotEmpty() && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userService.userDetailsService().loadUserByUsername(username)

            // Если токен валиден, то аутентифицируем пользователя
            if (jwtService.isTokenValid(jwt, userDetails)) {
                val context = SecurityContextHolder.createEmptyContext()
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }
        }

        filterChain.doFilter(request, response)
    }
}