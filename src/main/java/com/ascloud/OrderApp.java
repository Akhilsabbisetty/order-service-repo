package com.ascloud;

import jakarta.persistence.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class OrderApp {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderApp(
            CartRepository cartRepository,
            OrderRepository orderRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/orders")
    public Map<String, String> status() {
        return Map.of("service", "order-service", "status", "running");
    }

    @PostMapping("/cart/add")
    public Map<String, Object> addToCart(@RequestBody CartRequest request) {
        CartItem item = new CartItem();
        item.userEmail = request.userEmail;
        item.productId = request.productId;
        item.productName = request.productName;
        item.productType = request.productType;
        item.price = request.price;
        item.quantity = request.quantity <= 0 ? 1 : request.quantity;

        cartRepository.save(item);

        return Map.of("status", "success", "message", "Item added to cart", "cartItemId", item.id);
    }

    @GetMapping("/cart/{email}")
    public List<CartItem> getCart(@PathVariable String email) {
        return cartRepository.findByUserEmail(email);
    }

    @DeleteMapping("/cart/{id}")
    public Map<String, String> deleteCartItem(@PathVariable Long id) {
        cartRepository.deleteById(id);
        return Map.of("status", "success", "message", "Item removed");
    }

    @PostMapping("/orders/place")
    public Map<String, Object> placeOrder(@RequestBody OrderRequest request) {
        OrderRecord order = new OrderRecord();
        order.userEmail = request.email;
        order.totalAmount = request.totalAmount;
        order.status = "PLACED";
        order.createdAt = LocalDateTime.now().toString();

        orderRepository.save(order);

        kafkaTemplate.send("orders", "order-created:" + order.id);

        return Map.of(
                "status", "success",
                "message", "Order placed successfully",
                "orderId", order.id,
                "amount", order.totalAmount
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}

@Entity
@Table(name = "cart_items")
class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String userEmail;
    public String productId;
    public String productName;
    public String productType;
    public Double price;
    public Integer quantity;
}

@Entity
@Table(name = "orders")
class OrderRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String userEmail;
    public Double totalAmount;
    public String status;
    public String createdAt;
}

interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserEmail(String userEmail);
}

interface OrderRepository extends JpaRepository<OrderRecord, Long> {
    List<OrderRecord> findByUserEmail(String userEmail);
}

class CartRequest {
    public String userEmail;
    public String productId;
    public String productName;
    public String productType;
    public Double price;
    public Integer quantity;
}

class OrderRequest {
    public String email;
    public Double totalAmount;
}
