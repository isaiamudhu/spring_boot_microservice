package com.example.gateway.filter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import reactor.core.publisher.Mono;

@Component
public class ResponseHeaderFilter extends AbstractGatewayFilterFactory<ResponseHeaderFilter.Config> {

	public ResponseHeaderFilter() {
		super(Config.class);
	}

	public static class Config {
		// Configuration properties for the filter (if any)
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			// Retrieve the headers from the request
			HttpHeaders headers = exchange.getRequest().getHeaders();
			List<String> tokenList = headers.get("token");

			// Check if the token header is missing or empty
			if (CollectionUtils.isEmpty(tokenList)) {
				// If token is not present, set the status to UNAUTHORIZED and return the
				// response
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete(); // End the request without forwarding
			}

			// If the token header exists, forward the request to the next filter in the
			// chain
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				// You can add further processing after the chain if needed
			}));
		};
	}
}
