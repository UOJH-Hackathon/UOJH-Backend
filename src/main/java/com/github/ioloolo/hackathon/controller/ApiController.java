package com.github.ioloolo.hackathon.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.hackathon.socket.WebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class ApiController {

	private final WebSocketHandler webSocketHandler;

	@PostMapping("/online")
	public boolean online() {
		return WebSocketHandler.arduino.isPresent();
	}
}
