package app.labs.service;

import app.labs.mapper.OrderMapper;
import app.labs.model.Order;
import app.labs.model.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final Random random = new Random();

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    // ✅ 랜덤 주문 생성 (개별 주문 75%, 그룹 주문 25%)
    public List<Order> createRandomOrders() {
        orderMapper.clearOrders(); // 기존 주문 삭제

        List<Product> products = orderMapper.getAllProducts();
        if (products.isEmpty()) {
            throw new RuntimeException("❌ 상품 데이터가 없습니다.");
        }

        Long maxOrderId = Optional.ofNullable(orderMapper.getMaxOrderId()).orElse(0L);
        int orderCount = random.nextInt(1001) + 1000; // ✅ 1000~2000개 주문 생성

        for (int i = 0; i < orderCount; ) {
            boolean isGroupedOrder = random.nextDouble() < 0.25; // ✅ 25% 확률로 그룹 주문 (기존 30% → 25%로 감소)
            String orderNum = generateOrderNum(); // ✅ 주문번호 생성
            String destination = getRandomDestination(); // ✅ 목적지 선택

            int batchSize = isGroupedOrder ? 2 : 1; // ✅ 그룹 주문은 최대 2개, 개별 주문은 1개
            for (int j = 0; j < batchSize && i < orderCount; j++, i++) {
                Product product = products.get(random.nextInt(products.size())); // 랜덤 상품 선택

                Order order = new Order();
                order.setOrderId(++maxOrderId);
                order.setOrderNum(orderNum);
                order.setOrderTime(LocalDateTime.now());
                order.setDestination(destination);
                order.setState("Pending");
                order.setProductProductId(product.getProductId());

                orderMapper.insertOrder(order);
            }
        }

        return orderMapper.getAllOrders(); // ✅ 생성된 모든 주문 반환
    }

    // ✅ 8자리 랜덤 orderNum 생성
    private String generateOrderNum() {
        return String.valueOf(10000000 + random.nextInt(90000000));
    }

    // ✅ 랜덤 목적지 선택
    private String getRandomDestination() {
        String[] destinations = {"캠프1", "캠프2", "캠프3", "캠프4", "캠프5"};
        return destinations[random.nextInt(destinations.length)];
    }

    // ✅ 특정 날짜의 주문 조회
    public List<Order> getOrdersByDate(String date) {
        return orderMapper.getOrdersByDate(date);
    }

    // ✅ 필터링된 주문 조회 (목적지 & 날짜)
    public List<Order> getOrders(String destination, String date) {
        if (destination == null && date == null) {
            return orderMapper.getAllOrders();
        } else if (destination != null && date == null) {
            return orderMapper.getOrdersByDestination(destination);
        } else if (destination == null) {
            return orderMapper.getOrdersByDate(date);
        } else {
            return orderMapper.getOrdersByDestinationAndDate(destination, date);
        }
    }
}
