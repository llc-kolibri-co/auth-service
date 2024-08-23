package ru.hammi.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.hammi.authservice.entity.UsersEntity


interface UsersRepository: JpaRepository<UsersEntity, Long> {

    fun findByUsername(username: String?): UsersEntity?
    fun existsByUsername(username: String?): Boolean
    fun existsByEmail(email: String?): Boolean
}