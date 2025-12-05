package com.ldlanches.demo.Security

import com.ldlanches.demo.Model.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImplementation( val user : User) :
    UserDetails {

    override fun getUsername() = user.email

    override fun getPassword() = user.password

    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_USER"))

}