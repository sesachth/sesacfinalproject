package app.labs.controller;

import app.labs.service.OrderService;
import app.labs.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ✅ 주문 페이지 로드 (Thymeleaf 렌더링)
    @GetMapping
    public String orderPage(Model model) {
        model.addAttribute("page", "order");
        return "thymeleaf/html/admin/admin_order";
    }

 // ✅ 주문 데이터 조회 API (JSON 반환)
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> getOrders(
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "2000") int size) {  // ✅ 기본값 20 설정

        List<Order> orders;
        int offset = (page - 1) * size;
        int totalCount;

        try {
            if (destination == null && date == null) {
                orders = orderService.getAllOrders(size, offset);
                totalCount = orderService.getTotalOrderCount();
            } else if (destination != null && date == null) {
                orders = orderService.getOrdersByDestination(destination, size, offset);
                totalCount = orderService.getTotalOrderCountByDestination(destination);
            } else if (destination == null && date != null) {
                String startOfDay = date + " 00:00:00";
                String endOfDay = date + " 23:59:59";
                orders = orderService.getOrdersByDate(startOfDay, endOfDay, size, offset);
                totalCount = orderService.getTotalOrderCountByDate(startOfDay, endOfDay);
            } else {
                String startOfDay = date + " 00:00:00";
                String endOfDay = date + " 23:59:59";
                orders = orderService.getOrdersByDestinationAndDate(destination, startOfDay, endOfDay, size, offset);
                totalCount = orderService.getTotalOrderCountByDestinationAndDate(destination, startOfDay, endOfDay);
            }

            if (orders.isEmpty()) {
                return ResponseEntity.status(404).body("❌ 주문이 존재하지 않습니다.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("totalCount", totalCount);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 주문 조회 중 오류 발생: " + e.getMessage());
        }
    }



    // ✅ 랜덤 주문 생성 API
    @RequestMapping(value = "/generate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateOrders() {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.generateRandomOrders();
            response.put("status", "success");
            response.put("message", "✅ 랜덤 주문 생성 완료!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ 주문 생성 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
