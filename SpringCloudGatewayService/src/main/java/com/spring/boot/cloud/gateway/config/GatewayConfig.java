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

	/*
	 * Things to implement in SCG are,
	 * f.rewritePath for custom routes
	 * CORS configuration
	 * Custom Error JSON Response
	 * Custom 404 Not Found Response
	 * Decorated Request and Decorator Response
	 */
	
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/getMessage/**").filters(f -> f.filter(jwtFilter)).uri("lb://spring-cloud-eureka-client"))
				.route(r -> r.path("/postMessage").filters(f -> f.filter(jwtFilter)).uri("lb://spring-cloud-eureka-client"))
				.route(r -> r.path("/auth/login").filters(f -> f.filter(jwtFilter)).uri("lb://spring-cloud-eureka-auth"))
				.route(r -> r.path("/auth/register").filters(f -> f.filter(jwtFilter)).uri("lb://spring-cloud-eureka-auth"))
				.build();
	}

}
