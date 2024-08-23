package ru.hammi.authservice.entity

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users", schema = "auth")
data class UsersEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
        @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
        val id: Long? = null,
        @Column(unique = true, nullable = false, name = "username")
        private val username: String,
        @Column(nullable = false, name = "password")
        private val password: String,
        @Column(nullable = false)
        val email: String,
        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        var role: Role

) : UserDetails {

    override fun getAuthorities(): List<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword() = this.password

    override fun getUsername() = this.username
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}