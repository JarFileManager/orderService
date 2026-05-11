package com.example.OrderService.controllers;

import com.example.OrderService.dtos.InventoryRequest;
import com.example.OrderService.dtos.OrderResponse;
import com.example.OrderService.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody InventoryRequest inventoryRequest) {
        OrderResponse orderResponse = orderService.tryPlacingOrder(inventoryRequest);
        if(orderResponse != null){
            return ResponseEntity.ok(orderResponse);
        }

        return ResponseEntity.ok().body(new OrderResponse(null, "FAILED"));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponses);
    }
}
