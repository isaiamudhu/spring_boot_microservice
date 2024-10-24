package com.example.inventoryservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventoryservice.entity.InventoryItem;
import com.example.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	// Get all inventory items
	@GetMapping(value = "/items", produces = { "application/json", "application/xml" })
	public ResponseEntity<List<InventoryItem>> getAllItems() {
		List<InventoryItem> items = inventoryService.getAllItems();
		return ResponseEntity.ok(items);
	}

	@PostMapping("/items")
	public void storeItems(@RequestBody InventoryItem inventoryItem) {
		inventoryService.store(inventoryItem);
	}

	@PostMapping("/all/items")
	public void storeAllItems(@RequestBody List<InventoryItem> inventoryItems) {
		inventoryService.storeAll(inventoryItems);
	}

	// Get a specific inventory item by ID
	@GetMapping("/items/{itemId}")
	public ResponseEntity<InventoryItem> getItemById(@PathVariable("itemId") Long itemId) {
		Optional<InventoryItem> item = inventoryService.getItemById(itemId);
		return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Check if an item is available
	@GetMapping("/items/{itemId}/available")
	public ResponseEntity<Boolean> isItemAvailable(@PathVariable("itemId") Long itemId,
			@RequestParam("quantity") int quantity) {
		boolean isAvailable = inventoryService.isItemAvailable(itemId, quantity);
		return ResponseEntity.ok(isAvailable);
	}

	// Reserve an item
	@PutMapping("/items/{itemId}/reserve")
	public ResponseEntity<Void> reserveItem(@PathVariable("itemId") Long itemId,
			@RequestParam("quantity") int quantity) {
		try {
			inventoryService.reserveItem(itemId, quantity);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Deduct an item after order completion
	@PutMapping("/items/{itemId}/deduct")
	public ResponseEntity<Void> deductItem(@PathVariable Long itemId, @RequestParam int quantity) {
		try {
			inventoryService.deductItem(itemId, quantity);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// Release a reserved item (cancel reservation)
	@PutMapping("/items/{itemId}/release")
	public ResponseEntity<Void> releaseItem(@PathVariable Long itemId, @RequestParam int quantity) {
		try {
			inventoryService.releaseItem(itemId, quantity);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
