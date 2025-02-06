package app.labs.repository;

import app.labs.model.Order;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final Map<Long, Order> orderStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderStore.values());
    }

    public Order saveOrder(Order order) {
        if (order.getOrderId() == null) {
            order.setOrderId(idGenerator.getAndIncrement()); // orderId 자동 증가
        }
        orderStore.put(order.getOrderId(), order);
        return order;
    }

    public List<Order> getOrdersByDate(String date) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if (order.getOrderTime().toLocalDate().toString().equals(date)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> filterOrders(Long orderId, String destination, String startTime, String endTime, Long productProductId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if ((orderId == null || order.getOrderId().equals(orderId)) &&
                (destination == null || order.getDestination().equals(destination)) &&
                (startTime == null || endTime == null || 
                 (order.getOrderTime().isAfter(LocalDateTime.parse(startTime)) &&
                  order.getOrderTime().isBefore(LocalDateTime.parse(endTime)))) &&
                (productProductId == null || order.getProductProductId().equals(productProductId))) {
                result.add(order);
            }
        }
        return result;
    }
}
