package ru.hammi.authservice.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.hammi.authservice.entity.Role
import ru.hammi.authservice.entity.UsersEntity
import ru.hammi.authservice.repository.UsersRepository


@Service
class UserService(
    private val repository: UsersRepository
) {

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    fun save(user: UsersEntity): UsersEntity? {
        return repository.save(user)
    }


    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    fun create(user: UsersEntity): UsersEntity? {
        if (repository.existsByUsername(user.username)) {
            // Заменить на свои исключения
            throw RuntimeException("Пользователь с таким именем уже существует")
        }
        if (repository.existsByEmail(user.email)) {
            throw RuntimeException("Пользователь с таким email уже существует")
        }
        return save(user)
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    fun getByUsername(username: String): UsersEntity {
        return repository.findByUsername(username) ?: throw UsernameNotFoundException("Пользователь не найден")
    }

    /**
     * Получение пользователя по имени пользователя
     *
     *
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username -> getByUsername(username) }
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    fun getCurrentUser(): UsersEntity {
        // Получение имени пользователя из контекста Spring Security
        val username = SecurityContextHolder.getContext().authentication.name
        return getByUsername(username)
    }


    /**
     * Выдача прав администратора текущему пользователю
     *
     *
     * Нужен для демонстрации
     */
    @Deprecated("")
    fun getAdmin() {
        val user = getCurrentUser()
        user.role = Role.ROLE_ADMIN
        save(user)
    }
}