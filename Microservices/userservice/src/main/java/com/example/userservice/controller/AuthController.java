package com.example.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.dto.AuthRequest;
import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.TokenValidationRequest;
import com.example.userservice.dto.TokenValidationResponse;
import com.example.userservice.service.CustomUserDetailsService;
import com.example.userservice.service.JWTUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTUtils jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthResponse(jwt));
	}

	@PostMapping("/validate")
	public ResponseEntity<?> validateToken(@RequestBody TokenValidationRequest request) {
		String token = request.getToken();
		String username = jwtUtil.extractUsername(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (jwtUtil.validateToken(token, userDetails)) {
			return ResponseEntity.ok().body(new TokenValidationResponse(true));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse(false));
		}
	}
}