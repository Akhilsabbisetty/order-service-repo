package com.ascloud;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class OrderApp {

    @Autowired
    private KafkaTemplate<String, String> kafka;

    @PostMapping("/orders")
    public Map<String, String> createOrder() {
        kafka.send("orders", "order-created");
        return Map.of("status", "order placed");
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}