package com.example.OrderService.configs;

import com.example.OrderService.dtos.InventoryResponse;
import com.example.OrderService.dtos.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "InventoryService")
public interface InventoryClient {
    @PostMapping("inventory/check-and-reduce")
    ResponseEntity<InventoryResponse> checkAndReduceStock(InventoryRequest inventoryRequest);
}
