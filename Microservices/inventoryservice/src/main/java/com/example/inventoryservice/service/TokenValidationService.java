package com.example.inventoryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.inventoryservice.dto.TokenValidationRequest;
import com.example.inventoryservice.dto.TokenValidationResponse;

@Service
public class TokenValidationService {

	@Autowired
	private RestTemplate restTemplate;

	public boolean validateTokenWithUserService(String token) {
		TokenValidationRequest request = new TokenValidationRequest();
		request.setToken(token);

		ResponseEntity<TokenValidationResponse> response = restTemplate
				.postForEntity("http://localhost:7075/api/auth/validate", request, TokenValidationResponse.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody().isValid();
		} else {
			return false;
		}
	}
}
