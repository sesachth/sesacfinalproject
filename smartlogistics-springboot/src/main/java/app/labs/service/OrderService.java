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

    // ✅ 새로운 주문 생성 메서드 (매개변수 추가)
    public List<Order> createRandomOrders(int minOrders, int maxOrders, double groupOrderRatio) {
        sqlSession.delete("mybatis.mappers.OrderMapper.clearOrders");

        List<OrderProduct> orderProducts = sqlSession.selectList("mybatis.mappers.OrderMapper.getAllProducts");
        if (orderProducts.isEmpty()) {
            throw new RuntimeException("❌ 상품 데이터가 없습니다.");
        }

        Object result = sqlSession.selectOne("mybatis.mappers.OrderMapper.getMaxOrderId");
        Long maxOrderId = (result != null) ? ((Number) result).longValue() : 0L;

        int orderCount = random.nextInt(maxOrders - minOrders + 1) + minOrders;

        for (int i = 0; i < orderCount; ) {
            boolean isGroupedOrder = random.nextDouble() < groupOrderRatio;
            String orderNum = generateOrderNum();  // ✅ 여기서 메서드 사용
            String destination = getRandomDestination();  // ✅ 여기서 메서드 사용

            int batchSize = isGroupedOrder ? 2 : 1;
            for (int j = 0; j < batchSize && i < orderCount; j++, i++) {
                OrderProduct orderProduct = orderProducts.get(random.nextInt(orderProducts.size()));

                Order order = new Order();
                order.setOrderId(++maxOrderId);
                order.setOrderNum(orderNum);
                order.setOrderTime(LocalDateTime.now());
                order.setDestination(destination);
                order.setState("Pending");
                order.setProductProductId(orderProduct.getProductId());

                sqlSession.insert("mybatis.mappers.OrderMapper.insertOrder", order);
            }
        }

        return sqlSession.selectList("mybatis.mappers.OrderMapper.getAllOrders");
    }

    // ✅ 8자리 랜덤 orderNum 생성 (메서드 추가)
    private String generateOrderNum() {
        return String.valueOf(10000000 + random.nextInt(90000000));
    }

    // ✅ 랜덤 목적지 선택 (메서드 추가)
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
