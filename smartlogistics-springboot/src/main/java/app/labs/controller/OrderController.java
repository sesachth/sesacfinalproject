package app.labs.controller;

import app.labs.model.Order;
import app.labs.service.OrderService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
                                       @RequestParam(value = "size", required = false, defaultValue = "5000") int size) {
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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            orders.forEach(order -> order.setFormattedOrderTime(order.getOrder_time().format(formatter)));

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
    @DeleteMapping("/delete/{order_id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long order_id) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.deleteOrder(order_id);
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
    public ResponseEntity<?> searchOrderByOrderNum(@RequestParam(name = "orderNum") String orderNum) {
        try {
            List<Order> orders = orderService.getOrdersByOrderNum(orderNum);

            if (orders == null || orders.isEmpty()) {
                return ResponseEntity.status(404).body("❌ 해당 주문번호로 조회된 주문이 없습니다.");
            }

            return ResponseEntity.ok(Collections.singletonMap("orders", orders));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 주문 조회 중 오류 발생: " + e.getMessage());
        }
    }


    // ✅ 주문 상태 업데이트 API
    @PutMapping("/update-status/{order_id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateOrderStatus(@PathVariable Long order_id, @RequestParam String status) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.updateOrderStatus(order_id, status);
            response.put("status", "success");
            response.put("message", "✅ 주문 상태 변경 완료!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "❌ 상태 변경 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 주문 목록 엑셀 다운로드 API
    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "date", required = false) String date) {

        List<Order> orders;

        try {
            if (destination == null && date == null) {
                orders = orderService.getAllOrders(5000, 0);
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

            if (orders.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ 다운로드할 주문이 없습니다.".getBytes(StandardCharsets.UTF_8));
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Orders");
                Row header = sheet.createRow(0);

                String[] columns = {"주문 ID", "주문 번호", "주문 날짜", "주문 시간", "목적지", "상품 ID"};
                for (int i = 0; i < columns.length; i++) {
                    header.createCell(i).setCellValue(columns[i]);
                }

                int rowIdx = 1;
                for (Order order : orders) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(order.getOrderId());
                    row.createCell(1).setCellValue(order.getOrderNum());
                    row.createCell(2).setCellValue(order.getOrderTime() != null ? order.getOrderTime().toLocalDate().toString() : "-");
                    row.createCell(3).setCellValue(order.getOrderTime() != null ? order.getOrderTime().toLocalTime().toString() : "-");
                    row.createCell(4).setCellValue(order.getDestination());
                    row.createCell(5).setCellValue(order.getProductId());
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(outputStream.toByteArray());
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("❌ 엑셀 생성 중 오류 발생: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

}
