package ru.hammi.authservice.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import ru.hammi.authservice.feign.dto.request.UserInfoRequest

@FeignClient(
    name = "user-service-feign-client",
    url = "\${module.user.service-url}"
)
interface UserServiceFeignClient {

    @PostMapping("/api/user/info")
    fun createUserInfo(userInfo: UserInfoRequest)
}