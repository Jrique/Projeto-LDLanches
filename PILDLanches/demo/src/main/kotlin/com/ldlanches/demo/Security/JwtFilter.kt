package com.ldlanches.demo.Security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter( val tokenService : TokenService,
                 val userDetailService : UserDetailsService
) : OncePerRequestFilter() {


    override fun doFilterInternal(
        request : HttpServletRequest, response : HttpServletResponse,
        filterChain : FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        var auth = false
        if (authHeader != null && authHeader.contains("Bearer")) {
            val token = authHeader.substring(7)
            val username = tokenService.extractUsername( token )
            val expired = tokenService.isTokenExpired( token )
            if ( !expired ) {
                val userDetails = userDetailService.loadUserByUsername( username )
                if ( userDetails != null ) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities )
                    SecurityContextHolder.getContext().authentication = authToken
                    auth = true
                }
            }
        }
        filterChain.doFilter( request, response )
    }



}