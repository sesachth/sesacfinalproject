package app.labs.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import app.labs.model.Order;

@Repository
public class OrderRepository {
    private final Map<Long, Order> orderStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderStore.values());
    }

    public Order saveOrder(Order order) {
        if (order.getOrder_id() == null) {
            order.setOrder_id(idGenerator.getAndIncrement()); // order_id 자동 증가
        }
        orderStore.put(order.getOrder_id(), order);
        return order;
    }

    public List<Order> getOrdersByDate(String date) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if (order.getOrder_time().toLocalDate().toString().equals(date)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> filterOrders(Long order_id, String destination, String startTime, String endTime, Long product_id) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if ((order_id == null || order.getOrder_id().equals(order_id)) &&
                (destination == null || order.getDestination().equals(destination)) &&
                (startTime == null || endTime == null || 
                 (order.getOrder_time().isAfter(LocalDateTime.parse(startTime)) &&
                  order.getOrder_time().isBefore(LocalDateTime.parse(endTime)))) &&
                (product_id == null || order.getProduct_id().equals(product_id))) {
                result.add(order);
            }
        }
        return result;
    }
}
