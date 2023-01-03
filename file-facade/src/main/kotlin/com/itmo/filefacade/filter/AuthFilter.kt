package com.itmo.filefacade.filter

import com.itmo.highload.feign.reactive_client.AuthClient
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthFilter(
    val authClient: AuthClient
) : WebFilter {

    private fun requestIsValid(request: ServerHttpRequest): Boolean {
        return request.headers[HttpHeaders.AUTHORIZATION] != null
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (requestIsValid(exchange.request)) {
            val userDTO =
                authClient.validateToken(exchange.request.headers[HttpHeaders.AUTHORIZATION]!![0]!!)
                    .block()
            exchange.request.headers["USER_ID"] = userDTO.id.toString()
            chain.filter(exchange)
        } else {
            onError(exchange, "bad request, no authorization header")
        }
    }

    private fun onError(exchange: ServerWebExchange, message: String): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.statusCode = HttpStatus.BAD_REQUEST
        response.headers["ERROR_REASON"] = message
        return response.setComplete()
    }
}
