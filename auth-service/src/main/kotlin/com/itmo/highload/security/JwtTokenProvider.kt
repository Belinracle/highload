package com.itmo.highload.security

import com.itmo.highload.exception.JwtAuthenticationException
import com.itmo.highload.feign.dto.UserDTO
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private var secretKey: String,

    @Value("\${jwt.header}")
    private val authorizationHeader: String,

    @Value("\${jwt.expiration}")
    private val validityInMilliseconds: Long,

    @Value("\${jwt.prefix:Bearer}")
    private val tokenPrefix: String
) {

    init {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray());
    }

    fun createToken(userDTO: UserDTO): String {
        val claims: Claims = Jwts.claims().setSubject(userDTO.email)
        claims["id"] = userDTO.id
        claims["roles"] = userDTO.roles
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return "$tokenPrefix " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            // try to remove "Bearer " at the beginning of token
            val prefix = token.split(" ")[0].trim();
            if (prefix != tokenPrefix) {
                throw JwtAuthenticationException("JWT token is invalid", HttpStatus.UNAUTHORIZED)
            }
            val cutToken = token.split(" ")[1].trim();
            val claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(cutToken)
            !claimsJws.body.expiration.before(Date())
        } catch (e: SignatureException) {
            throw JwtAuthenticationException("Invalid JWT signature");
        } catch (e: MalformedJwtException) {
            throw JwtAuthenticationException("Invalid JWT token");
        } catch (e: ExpiredJwtException) {
            throw JwtAuthenticationException("JWT token is expired");
        } catch (e: UnsupportedJwtException) {
            throw JwtAuthenticationException("JWT token is unsupported");
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT claims string is empty");
        }
    }

    fun getUsername(token: String?): String? {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }
}