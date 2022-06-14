package com.spring.boot.auth.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.boot.auth.util.JwtUtil;


@CrossOrigin(origins = "*")
@RestController
public class AuthController {
 
	@Autowired
	private JwtUtil jwtUtil;
	
	
	@PostMapping("/auth/login")
	public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
		String userName = body.get("username");
		String password = body.get("password");
		
		/*
	 	 * Business Logic to validate credentials
	 	 * Post Successful validation generate JWT Token
	 	*/
		
		String token = jwtUtil.generateToken(userName,password);	
		return new ResponseEntity<String>(token, HttpStatus.OK);
	}

	
	@PostMapping("/auth/register")
	public ResponseEntity<String> register(@RequestBody String userName) {
		/*
		 *  Persist user to some persistent storage
		 *  Unimplemented Method
		 */		
		return new ResponseEntity<String>("User Registered Succesfuly", HttpStatus.OK);
	}
}

