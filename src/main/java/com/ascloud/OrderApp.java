package com.ascloud;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@RestController
public class OrderApp {

    @Autowired
    private KafkaTemplate<String, String> kafka;

    @PostMapping("/orders")
    public String createOrder() {
        kafka.send("orders", "order-created");
        return "Order created";
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}