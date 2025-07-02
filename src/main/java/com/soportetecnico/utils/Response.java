package com.soportetecnico.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
	
	public static ResponseEntity<Map<String, Object>> crearResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value()); 
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, status);
    }


}
