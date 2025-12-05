package com.ldlanches.demo.Controller


import com.ldlanches.demo.DTO.UserDTO
import com.ldlanches.demo.Model.User
import com.ldlanches.demo.Security.TokenService
import com.ldlanches.demo.Security.UserDetailsServiceImplementation
import com.ldlanches.demo.Service.UserService
import com.ldlanches.demo.DTO.LoginResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Calendar

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/user")
class UsuarioController(
    val tokenService : TokenService,
    val service : UserDetailsServiceImplementation,
    val userService:UserService) {

    fun generateToken(usuarioDTO : UserDTO) : String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 2)
        val d = cal.time
        val token = tokenService.generateToken(usuarioDTO.username, d )
        return token
    }
    @Operation(summary = "Cria um novo usuário", description = "Registra um novo usuário no sistema.")
    @PostMapping("/signup", produces = ["application/json"])
    fun signup(@Valid @RequestBody usuarioDTO: UserDTO): ResponseEntity<LoginResponse> {
        val createdUser = service.signup(
            usuarioDTO.username,
            usuarioDTO.password,
            usuarioDTO.name
        )

        val token = generateToken(usuarioDTO)

        val response = LoginResponse(
            token = token,
            username = createdUser.email,
            name = createdUser.name,
            id = createdUser.id ?: 0L
        )

        return ResponseEntity.ok(response)
    }
    @Operation(summary = "Verifica as credenciais do usuário", description = "Verifica o email e senha do usuário e retorna o token")
    @PostMapping("/signin", produces = ["application/json"])
    fun signin(@Valid @RequestBody usuarioDTO: UserDTO): ResponseEntity<LoginResponse> {
        service.loadUserByUsername(usuarioDTO.username)

        val user = userService.findByEmail(usuarioDTO.username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val token = generateToken(usuarioDTO)

        val response = LoginResponse(
            token = token,
            username = user.email,
            name = user.name,
            id = user.id ?: 0L
        )

        return ResponseEntity.ok(response)
    }
    @Operation(summary = "Atualiza os dados de um usuário", description = "Atualiza um usuário no sistema")
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody dto: UserDTO): ResponseEntity<User> {
        val success = userService.updateUser(id, dto)
        return success
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
    @Operation(summary = "Lista todos os usuários de um sistema", description = "Retorna uma lista com todos os usuários cadastrados no sistema")
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity(users,HttpStatus.OK)
    }
    @Operation(summary = "Exclui um usuário", description = "Remove um usuário do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity(HttpStatus.OK)
    }

}