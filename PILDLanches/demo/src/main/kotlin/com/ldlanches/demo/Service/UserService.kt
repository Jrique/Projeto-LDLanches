package com.ldlanches.demo.Service


import com.ldlanches.demo.DAO.UserDAO
import com.ldlanches.demo.DTO.UserDTO
import com.ldlanches.demo.Model.User
import com.ldlanches.demo.Repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    fun createUser(userDTO: UserDAO): User {
        val user = User(
            email = userDTO.email,
            password = passwordEncoder.encode(userDTO.password),
            name = userDTO.name,
            perfil = userDTO.perfil
        )
        return userRepository.save(user)
    }

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: Long): Optional<User> = userRepository.findById(id)

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }
    fun updateUser(id: Long, userDTO: UserDTO): User? {
        return if (userRepository.existsById(id)) {
            val user = User(
                id = id,
                email = userDTO.username,
                password = userDTO.password,
                name = userDTO.name,
                perfil = userDTO.perfil
            )
            userRepository.save(user)
        } else {
            null
        }
    }

    fun deleteUser(id: Long) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
        }
    }
}