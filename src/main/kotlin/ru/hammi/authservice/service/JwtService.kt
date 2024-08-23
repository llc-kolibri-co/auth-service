package ru.hammi.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import ru.hammi.authservice.controller.dto.response.CheckTokenAuthenticationResponse
import ru.hammi.authservice.entity.UsersEntity
import java.security.Key
import java.util.*

@Service
class JwtService(
    private val userService: UserService
) {
    @Value("\${token.signing.key}")
    private lateinit var jwtSigningKey: String

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    fun extractUserName(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    fun generateToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        if (userDetails is UsersEntity) {
            claims["id"] = userDetails.id!!
            claims["email"] = userDetails.email
            claims["role"] = userDetails.role
        }
        return generateToken(claims, userDetails)
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userName = extractUserName(token)
        return (userName == userDetails.username) && !isTokenExpired(token);
    }

    fun validateToken(token: String): CheckTokenAuthenticationResponse {
        return try {
            val userName = extractUserName(token)
            val userDetails = userService.userDetailsService().loadUserByUsername(userName)
            val isAuthenticated = (userName == userDetails.username) && !isTokenExpired(token)

            CheckTokenAuthenticationResponse(isAuthenticated = isAuthenticated, userDetails = userDetails)
        } catch (e: Exception) {
            CheckTokenAuthenticationResponse(isAuthenticated = false)
        }
    }


    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    private fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.username)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return ключ
     */
    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}