package com.example.userservice.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.userservice.dto.UserRegistrationDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User registerUser(UserRegistrationDto registrationDto) {
		User user = new User();
		user.setUsername(registrationDto.getUsername());
		user.setEmail(registrationDto.getEmail());
		user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
		user.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN")); // Default role
		return userRepository.save(user);
	}
}