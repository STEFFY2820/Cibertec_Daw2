package com.soportetecnico.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class Test {
	
	@GetMapping("/mensaje")
	public String test() {
		return "API is running!";
	}

}
