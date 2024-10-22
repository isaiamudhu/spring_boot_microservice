package com.example.orderservice.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventoryservice", url = "http://localhost:8888/inventory")
public interface IntentoryClient {
	
	@GetMapping(path = "/api/inventory/items/{itemId}/available")
	public Boolean checkInventory(@PathVariable("itemId") long itemId, @RequestParam("quantity") int quantity);
	
	@PutMapping(path = "/api/inventory/items/{itemId}/reserve")
	public void reserveQuantity(@PathVariable("itemId") long itemId, @RequestParam("quantity") int quantity);
}
