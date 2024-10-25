package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.InventoryItem;
import com.example.inventoryservice.repository.InventoryRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Get all inventory items
    public List<InventoryItem> getAllItems() {
    	log.info("get all inventory items request received");
        return inventoryRepository.findAll();
    }

    // Get a specific inventory item by ID
    public Optional<InventoryItem> getItemById(Long itemId) {
        return inventoryRepository.findById(itemId);
    }

    // Check if an item is available in the required quantity
    @Cacheable(cacheNames = {"cache1"}, key = "itemId")
    public boolean isItemAvailable(Long itemId, int requiredQuantity) {
    	log.info("request received for checking inventory item with id {}", itemId);
        Optional<InventoryItem> item = inventoryRepository.findById(itemId);
        return item.map(inventoryItem -> inventoryItem.getQuantity() >= requiredQuantity).orElse(false);
    }

    // Reserve an item by reducing the available quantity
    public void reserveItem(Long itemId, int quantity) {
    	log.info("request received for reserving inventory item with id {}", itemId);
        Optional<InventoryItem> item = inventoryRepository.findById(itemId);
        if (item.isPresent()) {
            InventoryItem inventoryItem = item.get();
            if (inventoryItem.getQuantity() >= quantity) {
                inventoryItem.setQuantity(inventoryItem.getQuantity() - quantity);
                inventoryRepository.save(inventoryItem);
            } else {
                throw new RuntimeException("Insufficient stock for item ID: " + itemId);
            }
        } else {
            throw new RuntimeException("Item not found: " + itemId);
        }
    }

    // Deduct the final inventory after the order is completed
    public void deductItem(Long itemId, int quantity) {
        Optional<InventoryItem> item = inventoryRepository.findById(itemId);
        if (item.isPresent()) {
            InventoryItem inventoryItem = item.get();
            inventoryItem.setQuantity(inventoryItem.getQuantity() - quantity);
            inventoryRepository.save(inventoryItem);
        } else {
            throw new RuntimeException("Item not found: " + itemId);
        }
    }

    // Release a reserved item (cancel reservation)
    public void releaseItem(Long itemId, int quantity) {
        Optional<InventoryItem> item = inventoryRepository.findById(itemId);
        if (item.isPresent()) {
            InventoryItem inventoryItem = item.get();
            inventoryItem.setQuantity(inventoryItem.getQuantity() + quantity);
            inventoryRepository.save(inventoryItem);
        } else {
            throw new RuntimeException("Item not found: " + itemId);
        }
    }

	public void store(InventoryItem inventoryItem) {
		inventoryRepository.save(inventoryItem);
	}

	public void storeAll(List<InventoryItem> inventoryItems) {
		inventoryRepository.saveAll(inventoryItems);
	}
}
