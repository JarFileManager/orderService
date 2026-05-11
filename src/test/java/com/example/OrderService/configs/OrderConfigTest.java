package com.example.OrderService.configs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class OrderConfigTest {

    private OrderConfig orderConfig;

    @BeforeEach
    public void init() {
        this.orderConfig = new OrderConfig();
    }

    @Test
    public void testConstructor(){
        RestTemplate restTemplate = orderConfig.restTemplate();
        Assertions.assertNotNull(restTemplate);
    }
}
