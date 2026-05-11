package com.example.OrderService.services;

import com.example.OrderService.configs.InventoryClient;
import com.example.OrderService.dtos.InventoryResponse;
import com.example.OrderService.dtos.InventoryRequest;
import com.example.OrderService.dtos.OrderResponse;
import com.example.OrderService.models.Order;
import com.example.OrderService.repositories.OrderRepository;
import com.example.OrderService.utils.OrderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.retry.annotation.Retryable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    private RestTemplate restTemplate;

    private InventoryClient inventoryClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.inventoryClient = inventoryClient;
    }

    @Retry(name = "inventory")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "inventoryFallback")
    public OrderResponse tryPlacingOrder(InventoryRequest inventoryRequest) {
        System.out.println("Inside tryPlacingOrder");
        if(checkAndReduceStock(inventoryRequest)){
            Order order = OrderUtils.generateOrder(inventoryRequest);
            try{
                order = orderRepository.save(order);
                return OrderUtils.generateOrderResponse(order);
            } catch(Exception e){
                boolean isReplenished = replenishInventory(inventoryRequest.getProductId(), inventoryRequest.getQuantity());
                if(isReplenished){
                    System.out.println("Replenished Order Successfully");
                }
                return null;
            }
        }

        return null;
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for(Order order : orders){
            orderResponses.add(OrderUtils.generateOrderResponse(order));
        }

        return orderResponses;
    }

    private boolean replenishInventory(Long productId, int quantity){
        try {
            URL url = new URL("http://localhost:8082/inventory/replenish-inventory");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON body
            String jsonInput = "{ \"productId\": " + productId + ", \"quantity\": " + quantity + " }";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();

            if (status == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8")
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean checkAndReduceStock(InventoryRequest inventoryRequest){
        ResponseEntity<InventoryResponse> responseEntity = inventoryClient.checkAndReduceStock(inventoryRequest);
        if(responseEntity.getBody() == null){
            return false;
        }
        return responseEntity.getBody().getIsAvailable();
    }

    private OrderResponse inventoryFallback(InventoryRequest inventoryRequest, Exception ex) {
        System.out.println("Fallback triggered: " + ex.getMessage());
        System.out.println("Inventory Request: " + inventoryRequest);
        System.out.println("Please try again after some time....");
        return null;
    }

    private boolean checkAndReduceStockRestTemplate(InventoryRequest inventoryRequest){
        String URL = "http://INVENTORYSERVICE/inventory/check-and-reduce";
        ResponseEntity<InventoryResponse> responseEntity = restTemplate.postForEntity(URL, inventoryRequest, InventoryResponse.class);
        if(responseEntity.getBody() == null){
            return false;
        }
        return responseEntity.getBody().getIsAvailable();
    }

    private boolean checkAndReduceStockPrimitive(Long productId, Integer quantity){
        try {
            URL url = new URL("http://localhost:8082/inventory/check-and-reduce");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON body
            String jsonInput = "{ \"productId\": " + productId + ", \"quantity\": " + quantity + " }";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();

            if (status == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8")
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                ObjectMapper mapper = new ObjectMapper();
                InventoryResponse inventoryResponse = mapper.readValue(response.toString(), InventoryResponse.class);
                return inventoryResponse.getIsAvailable();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
