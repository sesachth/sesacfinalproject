package app.labs.controller;

import app.labs.service.OrderService;
import app.labs.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ 주문 전체 조회
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) String date
    ) {
        List<Order> orders;

        try {
            if (destination == null && date == null) {
                orders = orderService.getAllOrders();
            } else if (destination != null && date == null) {
                orders = orderService.getOrdersByDestination(destination);
            } else if (destination == null && date != null) {
                String startOfDay = date + " 00:00:00";
                String endOfDay = date + " 23:59:59";
                orders = orderService.getOrdersByDate(startOfDay, endOfDay);
            } else {
                String startOfDay = date + " 00:00:00";
                String endOfDay = date + " 23:59:59";
                orders = orderService.getOrdersByDestinationAndDate(destination, startOfDay, endOfDay);
            }

            if (orders.isEmpty()) {
                return ResponseEntity.status(404).body("❌ 주문이 존재하지 않습니다.");
            }

            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 주문 조회 중 오류 발생: " + e.getMessage());
        }
    }

    // ✅ 랜덤 주문 생성 (GET & POST 지원)
    @RequestMapping(value = "/generate", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> generateOrders() {
        try {
            orderService.generateRandomOrders();
            return ResponseEntity.ok("✅ 랜덤 주문 생성 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 주문 생성 중 오류 발생: " + e.getMessage());
        }
    }
}
