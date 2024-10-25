package com.example.inventoryservice.config;

import java.util.Arrays;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate getRestTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	public CacheManager cacheManager() {
	    SimpleCacheManager cacheManager = new SimpleCacheManager();
	    cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("cache1")));
	    return cacheManager;
	}
}
