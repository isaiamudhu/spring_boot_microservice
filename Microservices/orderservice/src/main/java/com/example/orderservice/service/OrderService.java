package com.example.orderservice.service;

import java.net.http.HttpRequest;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.feignclient.IntentoryClient;
import com.netflix.discovery.converters.Auto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MessageProducer messageProducer;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private IntentoryClient inventoryClient;

	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	AuthService authService;

	// @CircuitBreaker(name = "inventoryservice", fallbackMethod = "defaultMethod")
	public Order placeOrder(Order order) {
		String authorization = "Basic dXNlcjpwYXNzd29yZA==";

		// Check inventory for each item
		for (int i = 0; i < order.getItemIds().size(); i++) {
			Long itemId = order.getItemIds().get(i);
			int quantity = order.getItemQuantities().get(i);

			Boolean isAvailable = inventoryClient.checkInventory(authorization, itemId, quantity);

			if (isAvailable == null || !isAvailable) {
				log.error("item not available");
				throw new RuntimeException("Item ID " + itemId + " is not available in the required quantity.");
			}

			inventoryClient.reserveQuantity(authorization, itemId, quantity);
//			// Reserve the inventory
//			restTemplate.put("http://localhost:9094/api/inventory/items/" + itemId + "/reserve?quantity=" + quantity,
//					null);
		}

		// Save order with status 'PENDING'
		order.setOrderStatus("PENDING");
//		messageProducer.sendMessage("order placed successfully for "+ order);
		Order orderEntity = orderRepository.save(order);
		log.info("data saved successfully {}", orderEntity);
		return orderEntity;
	}

	public void completeOrder(Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found."));

		// Deduct inventory after order completion
		for (int i = 0; i < order.getItemIds().size(); i++) {
			Long itemId = order.getItemIds().get(i);
			int quantity = order.getItemQuantities().get(i);

			restTemplate.put(discoveryClient.getInstances("inventoryservice").get(0).getUri() + "/" + itemId
					+ "/deduct?quantity=" + quantity, null);
		}

		// Update order status to 'COMPLETED'
		order.setOrderStatus("COMPLETED");
		orderRepository.save(order);
	}

	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found."));

		// Release the reserved inventory
		for (int i = 0; i < order.getItemIds().size(); i++) {
			Long itemId = order.getItemIds().get(i);
			int quantity = order.getItemQuantities().get(i);

			restTemplate.put(discoveryClient.getInstances("inventoryservice").get(0).getUri() + "/" + itemId
					+ "/release?quantity=" + quantity, null);
		}

		// Update order status to 'CANCELLED'
		order.setOrderStatus("CANCELLED");
		orderRepository.save(order);
	}

	public Order defaultMethod(Exception e) {
		log.info("called from circuit breaker {}", ExceptionUtils.getStackTrace(e));
		// send the order details to inventory service using queue
		return new Order();
		// logic as per your business needs
	}
}
