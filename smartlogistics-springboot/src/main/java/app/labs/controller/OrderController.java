package app.labs.controller;

import app.labs.model.Order;
import app.labs.service.OrderService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    public ResponseEntity<?> getOrders(@RequestParam(value = "destination", required = false) String destination,
                                       @RequestParam(value = "date", required = false) String date,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        List<Order> orders;
        int offset = (page - 1) * size;
        int totalCount;

        try {
            // ✅ 날짜 포맷 변환
            String startOfDay = null;
            String endOfDay = null;
            if (date != null && !date.isEmpty()) {
                startOfDay = date + " 00:00:00";
                endOfDay = date + " 23:59:59";
            }

            // ✅ 필터 조건에 따라 조회
            if (destination == null && date == null) {
                orders = orderService.getAllOrders(size, offset);
                totalCount = orderService.getTotalOrderCount();
            } else if (destination != null && date == null) {
                orders = orderService.getOrdersByDestination(destination, size, offset);
                totalCount = orderService.getTotalOrderCountByDestination(destination);
            } else if (destination == null) {
                orders = orderService.getOrdersByDate(startOfDay, endOfDay, size, offset);
                totalCount = orderService.getTotalOrderCountByDate(startOfDay, endOfDay);
            } else {
                orders = orderService.getOrdersByDestinationAndDate(destination, startOfDay, endOfDay, size, offset);
                totalCount = orderService.getTotalOrderCountByDestinationAndDate(destination, startOfDay, endOfDay);
            }

            // ✅ 날짜 및 시간 변환 (NULL 처리 추가)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            orders.forEach(order -> {
                if (order.getOrderTime() != null) {
                    order.setFormattedOrderTime(order.getOrderTime().format(formatter));
                } else {
                    order.setFormattedOrderTime("-");  // ✅ NULL이면 기본값으로 '-'
                }
            });

            // ✅ 응답 데이터 생성
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
    @PostMapping("/generate")
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

    // ✅ 주문 삭제 API
    @DeleteMapping("/delete/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long orderId) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.deleteOrder(orderId);
            response.put("status", "success");
            response.put("message", "✅ 주문 삭제 완료!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ 주문 삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
 // ✅ 주문번호로 주문 조회 API (JSON 반환)
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<?> searchOrderByNum(@RequestParam(name = "orderNum") String orderNum) {
        try {
            List<Order> orders = orderService.getOrdersByOrderNum(orderNum);
            if (orders.isEmpty()) {
                return ResponseEntity.ok(Map.of("orders", List.of(), "message", "❌ 해당 주문번호로 조회된 주문이 없습니다."));
            }
            return ResponseEntity.ok(Map.of("orders", orders, "total", orders.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류 발생", "message", e.getMessage()));
        }
    }


    // ✅ 주문 상태 업데이트 API
    @PutMapping("/update-status/{orderId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.updateOrderStatus(orderId, status);
            response.put("status", "success");
            response.put("message", "✅ 주문 상태 변경 완료!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ 상태 변경 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) String date) {

        List<Order> orders;

        if (destination == null && date == null) {
            orders = orderService.getAllOrders(5000, 0); // 최대 5000개까지 다운로드
        } else if (destination != null && date == null) {
            orders = orderService.getOrdersByDestination(destination, 5000, 0);
        } else if (destination == null && date != null) {
            String startOfDay = date + " 00:00:00";
            String endOfDay = date + " 23:59:59";
            orders = orderService.getOrdersByDate(startOfDay, endOfDay, 5000, 0);
        } else {
            String startOfDay = date + " 00:00:00";
            String endOfDay = date + " 23:59:59";
            orders = orderService.getOrdersByDestinationAndDate(destination, startOfDay, endOfDay, 5000, 0);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Orders");
            Row header = sheet.createRow(0);

            String[] columns = {"주문 ID", "주문 번호", "주문 날짜", "주문 시간", "목적지", "상품 ID", "상태"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(order.getOrderId());
                row.createCell(1).setCellValue(order.getOrderNum());
                row.createCell(2).setCellValue(order.getOrderTime().toLocalDate().toString());
                row.createCell(3).setCellValue(order.getOrderTime().toLocalTime().toString());
                row.createCell(4).setCellValue(order.getDestination());
                row.createCell(5).setCellValue(order.getProductId());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
