package com.example.orderservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.orderservice.config.dto.AuthRequest;
import com.example.orderservice.config.dto.AuthResponse;

@Service
public class AuthService {

	@Autowired
	private RestTemplate restTemplate;

	public String getJwtToken(String username, String password) {
		AuthRequest authRequest = new AuthRequest(username, password);

		ResponseEntity<AuthResponse> response = restTemplate.postForEntity("http://localhost:7075/api/auth/login",
				authRequest, AuthResponse.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody().getJwt();
		} else {
			throw new RuntimeException("Failed to authenticate with User Service");
		}
	}
}
