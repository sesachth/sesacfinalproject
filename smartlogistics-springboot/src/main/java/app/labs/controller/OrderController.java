package app.labs.controller;

import app.labs.model.Order;
import app.labs.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	/*
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(OrderController.class.getName());

    public OrderController(OrderService orderService) {
        this.orderService = orderService;

        // ✅ LocalDateTime 직렬화 + JSON 예쁘게 출력
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // ✅ LocalDateTime 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ✅ ISO 8601 포맷 유지
                .enable(SerializationFeature.INDENT_OUTPUT); // ✅ JSON 예쁘게 출력 (Pretty Print)
    }

    // ✅ 전체 주문 리스트 조회 API (필터링 가능)
    @GetMapping
    public ResponseEntity<String> getOrders(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String date) {
        try {
            List<Order> orders = orderService.getOrders(destination, date);
            return ResponseEntity.ok(objectMapper.writeValueAsString(orders));
        } catch (Exception e) {
            logger.severe("🚨 Error fetching orders: " + e.getMessage());
            return ResponseEntity.status(500).body("{\"error\": \"Failed to fetch orders\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    
    // ✅ 랜덤 주문 생성 API
    @GetMapping("/generate")
    public ResponseEntity<String> generateOrders() {
        try {
            List<Order> orders = orderService.createRandomOrders();
            return ResponseEntity.ok(objectMapper.writeValueAsString(orders));
        } catch (Exception e) {
            logger.severe("🚨 Failed to generate orders: " + e.getMessage());
            return ResponseEntity.status(500).body("{\"error\": \"Failed to generate orders\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
    */
}
