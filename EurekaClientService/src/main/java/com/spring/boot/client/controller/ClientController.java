package com.spring.boot.client.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

@CrossOrigin(origins = "*")
@RestController
public class ClientController {

	public Map<String,String> msgStore = new HashMap<String,String>();
	
	private boolean isValidUser(String userName){
		
		/*
		 * Business Logic to validate if the given user has rights to access the application
		*/
		return true;
		
	}
	

	@GetMapping("/getMessage/{msgid}")
	public ResponseEntity<String> getMessage(@PathVariable String msgid, @RequestHeader Map<String,String> header) {
		 String user = header.get("id").toString();
		 String responseMessage = null;
		 
		 if(!isValidUser(user)) {
			  JsonObject errorResponse = new JsonObject();
			  errorResponse.addProperty("message", "Unauthorized User Access");
		  return new ResponseEntity<String>(errorResponse.toString(),HttpStatus.UNAUTHORIZED);
		  }  
		 
		 if(msgStore.get(msgid)==null) {
			  responseMessage = "Not such Message ID exist";
		  }
		  else {
			  responseMessage = msgStore.get(msgid);
		  }
		return new ResponseEntity<String>("Client Service >>> "+responseMessage, HttpStatus.OK);
	}

	
	 @PostMapping("/postMessage") 
	 public ResponseEntity<String> postMessage(@RequestHeader Map<String,String> header, @RequestBody Map<String,String> body) {
	  
	  final String user = header.get("id").toString();
	  
	  if(!isValidUser(user)) {
		  JsonObject obj = new JsonObject();
		  obj.addProperty("message", "Unauthorized User Access");
	  return new ResponseEntity<String>(obj.toString(), HttpStatus.UNAUTHORIZED);
	  }
	  
	  msgStore.put(body.get("id"), body.get("message"));
	  return new ResponseEntity<String>("Message Posted Succesfuly with Id: "+body.get("id"), HttpStatus.ACCEPTED);
	  
	  }
	
}
