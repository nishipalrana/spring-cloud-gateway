package com.spring.boot.cloud.gateway.filter;


import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.gson.JsonObject;
import com.spring.boot.cloud.gateway.exception.JwtTokenMalformedException;
import com.spring.boot.cloud.gateway.exception.JwtTokenMissingException;
import com.spring.boot.cloud.gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

	@Autowired
	private JwtUtil jwtUtil;
	
	//Creating the Logger object
    Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    public Mono<Void> sendErrorResponse(ServerWebExchange exchange, String errMessage){
    	
    	ServerHttpRequest request = exchange.getRequest();
    	ServerHttpResponse response = exchange.getResponse();
    	
    	response.setStatusCode(HttpStatus.UNAUTHORIZED);
    	response.getHeaders().add("Content-Type", "application/json");
   
    	byte[] bytes = getJsonResponseObj(errMessage, HttpStatus.UNAUTHORIZED.value(), request.getPath().toString(), request.getId()).toString().getBytes(StandardCharsets.UTF_8);
    	DataBuffer buffer = response.bufferFactory().wrap(bytes);
    	return response.writeWith(Flux.just(buffer));
    	}
    
    public JsonObject getJsonResponseObj(String errorMessage,int errorCode, String path, String requestId) {
    	JsonObject errResponse = new JsonObject();
    	errResponse.addProperty("message", errorMessage);
    	errResponse.addProperty("status", errorCode);
    	errResponse.addProperty("path", path);
    	errResponse.addProperty("requestId", requestId);
    	return errResponse;
    }
    
    
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

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			logger.info("Post Filter -> Executes after the response is received from the called service");
		}));
	}

}
