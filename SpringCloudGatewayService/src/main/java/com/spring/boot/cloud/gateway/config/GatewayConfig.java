package com.spring.boot.cloud.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring.boot.cloud.gateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig extends GatewayAutoConfiguration{

	@Autowired
	private JwtAuthenticationFilter jwtFilter;
	
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/client-service/getMessage/**").filters(f -> f.rewritePath("/client-service/(?<path>.*)","/${path}").filter(jwtFilter)).uri("lb://spring-cloud-eureka-client"))
				.route(r -> r.path("/client-service/postMessage").filters(f -> f.rewritePath("/client-service/(?<path>.*)","/${path}").filter(jwtFilter)).uri("lb://spring-cloud-eureka-client"))
				.route(r -> r.path("/auth-service/auth/login").filters(f -> f.rewritePath("/auth-service/(?<path>.*)","/${path}").filter(jwtFilter)).uri("lb://spring-cloud-eureka-auth"))
				.route(r -> r.path("/auth-service/auth/register").filters(f -> f.rewritePath("/auth-service/(?<path>.*)","/${path}").filter(jwtFilter)).uri("lb://spring-cloud-eureka-auth"))
				.build();
	}

}
