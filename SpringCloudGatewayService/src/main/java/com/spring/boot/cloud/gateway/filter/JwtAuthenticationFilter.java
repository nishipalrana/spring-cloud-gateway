package com.spring.boot.cloud.gateway.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.spring.boot.cloud.gateway.exception.JwtTokenMalformedException;
import com.spring.boot.cloud.gateway.exception.JwtTokenMissingException;
import com.spring.boot.cloud.gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

	@Autowired
	private JwtUtil jwtUtil;
	
	//Creating the Logger object
    Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
		logger.info("Gateway Pre Filter executed");
		if  (!(request.getURI().getPath().contains("login") || request.getURI().getPath().contains("register"))){
			logger.info("In If Block");
			if (!request.getHeaders().containsKey("Authorization")) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}

			final String token = request.getHeaders().getOrEmpty("Authorization").get(0);

			try {
				jwtUtil.validateToken(token);
			} catch (JwtTokenMalformedException | JwtTokenMissingException e) {
				logger.info("Exception "+e.getMessage());
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				return response.setComplete();
			}

			Claims claims = jwtUtil.getClaims(token);
			System.out.println(claims.toString());
			exchange.getRequest().mutate().header("id", String.valueOf(claims.getSubject())).build();
			
		}

		return chain.filter(exchange);
	}

}
