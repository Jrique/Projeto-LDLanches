package com.ldlanches.demo.Security

import com.ldlanches.demo.Model.User
import com.ldlanches.demo.Repository.UserRepository
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImplementation(
    val repository: UserRepository,
    @Lazy val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username != null) {
            val user = repository.findByEmail(username)
            if (user.isPresent) {
                return UserDetailsImplementation(user.get())
            }
        }
        throw UsernameNotFoundException("Usuário não encontrado: $username")
    }

    fun signup(username: String, password: String, name: String): User {
        val user = User(
            email = username,
            password = passwordEncoder.encode(password),
            name = name,
            perfil = "ADMIN"
        )
        return repository.save(user)
    }
}