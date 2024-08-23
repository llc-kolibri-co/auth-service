package ru.hammi.authservice.feign.dto.request

data class UserInfoRequest(
    val userId: Long?,
    val username: String?,
    val email: String?
)