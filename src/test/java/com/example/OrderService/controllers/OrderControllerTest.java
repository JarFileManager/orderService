package com.example.OrderService.controllers;

import com.example.OrderService.dtos.InventoryRequest;
import com.example.OrderService.dtos.OrderResponse;
import com.example.OrderService.services.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private InventoryRequest inventoryRequest;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void init(){
        orderController = new OrderController(orderService);
    }

    @Test
    public void placeOrderSuccessTest(){
        OrderResponse orderResponse = new OrderResponse(123456L, "CREATED");
        Mockito.when(orderService.tryPlacingOrder(inventoryRequest)).thenReturn(orderResponse);
        ResponseEntity<OrderResponse> responseEntity = orderController.placeOrder(inventoryRequest);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(orderResponse, responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(orderResponse.getOrderId(), responseEntity.getBody().getOrderId());
        Assertions.assertEquals(orderResponse.getStatus(), responseEntity.getBody().getStatus());

    }

    @Test
    public void placeOrderFailTest(){
        Mockito.when(orderService.tryPlacingOrder(inventoryRequest)).thenReturn(null);
        ResponseEntity<OrderResponse> responseEntity = orderController.placeOrder(inventoryRequest);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNull(responseEntity.getBody().getOrderId());
        Assertions.assertEquals("FAILED", responseEntity.getBody().getStatus());
    }

    @Test
    public void getAllOrdersEmptyTest(){
        Mockito.when(orderService.getAllOrders()).thenReturn(new ArrayList<>());
        ResponseEntity<List<OrderResponse>> responseEntity = orderController.getAllOrders();
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody().isEmpty());
    }

    @Test
    public void getAllOrdersNullTest(){
        Mockito.when(orderService.getAllOrders()).thenReturn(null);
        ResponseEntity<List<OrderResponse>> responseEntity = orderController.getAllOrders();
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNull(responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void getAllOrdersSuccessTest(){
        List<OrderResponse> orderResponses = new ArrayList<>();
        OrderResponse orderResponse = new OrderResponse(123456L, "CREATED");
        orderResponses.add(orderResponse);
        Mockito.when(orderService.getAllOrders()).thenReturn(orderResponses);
        ResponseEntity<List<OrderResponse>> responseEntity = orderController.getAllOrders();
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody().containsAll(orderResponses));
    }
}
