package com.example.OrderService.utils;

import com.example.OrderService.dtos.InventoryRequest;
import com.example.OrderService.dtos.OrderResponse;
import com.example.OrderService.models.Order;

public class OrderUtils {

    public static Order generateOrder(InventoryRequest inventoryRequest) {
        Order order = new Order();
        order.setProductId(inventoryRequest.getProductId());
        order.setQuantity(inventoryRequest.getQuantity());
        order.setStatus("PLACED");
        return order;
    }

    public static OrderResponse generateOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setStatus(order.getStatus());
        return orderResponse;
    }
}
