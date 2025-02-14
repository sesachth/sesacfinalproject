package app.labs.service;

import app.labs.model.Order;
import app.labs.model.Product;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class OrderService {
    private final SqlSession sqlSession;
    private final Random random = new Random();

    public OrderService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    // ✅ 주문 ID로 단건 조회
    public Order getOrderById(Long order_id) {
        return sqlSession.selectOne("smartlogistics.OrderMapper.getOrderById", order_id);
    }

    // ✅ 전체 주문 개수 조회
    public int getTotalOrderCount() {
        return sqlSession.selectOne("smartlogistics.OrderMapper.getTotalOrderCount");
    }

    // ✅ 특정 목적지의 주문 개수 조회
    public int getTotalOrderCountByDestination(String destination) {
        Map<String, Object> params = new HashMap<>();
        params.put("destination", destination);
        return sqlSession.selectOne("smartlogistics.OrderMapper.getTotalOrderCountByDestination", params);
    }

    // ✅ 특정 날짜의 주문 개수 조회
    public int getTotalOrderCountByDate(String startOfDay, String endOfDay) {
        Map<String, Object> params = new HashMap<>();
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);
        return sqlSession.selectOne("smartlogistics.OrderMapper.getTotalOrderCountByDate", params);
    }

    // ✅ 특정 목적지 + 날짜의 주문 개수 조회
    public int getTotalOrderCountByDestinationAndDate(String destination, String startOfDay, String endOfDay) {
        Map<String, Object> params = new HashMap<>();
        params.put("destination", destination);
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);
        return sqlSession.selectOne("smartlogistics.OrderMapper.getTotalOrderCountByDestinationAndDate", params);
    }

    // ✅ 전체 주문 조회 (페이징)
    public List<Order> getAllOrders(int size, int offset) {
        Map<String, Object> params = new HashMap<>();
        params.put("size", size);
        params.put("offset", offset);
        return sqlSession.selectList("smartlogistics.OrderMapper.getAllOrders", params);
    }

    // ✅ 특정 목적지의 주문 조회 (페이징)
    public List<Order> getOrdersByDestination(String destination, int size, int offset) {
        Map<String, Object> params = new HashMap<>();
        params.put("destination", destination);
        params.put("size", size);
        params.put("offset", offset);
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByDestination", params);
    }

    // ✅ 특정 날짜의 주문 조회 (페이징)
    public List<Order> getOrdersByDate(String startOfDay, String endOfDay, int size, int offset) {
        Map<String, Object> params = new HashMap<>();
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);
        params.put("size", size);
        params.put("offset", offset);
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByDate", params);
    }

    // ✅ 특정 목적지 & 날짜 범위의 주문 조회 (페이징)
    public List<Order> getOrdersByDestinationAndDate(String destination, String startOfDay, String endOfDay, int size, int offset) {
        Map<String, Object> params = new HashMap<>();
        params.put("destination", destination);
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);
        params.put("size", size);
        params.put("offset", offset);
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByDestinationAndDate", params);
    }

    // ✅ 주문 삭제
    public void deleteOrder(Long order_id) {
        sqlSession.delete("smartlogistics.OrderMapper.deleteOrder", order_id);
    }
    
    // ✅ 기존 데이터 삭제 + AUTO_INCREMENT 초기화
    public void resetOrders() {
        sqlSession.delete("smartlogistics.OrderMapper.deleteAllOrders");
        sqlSession.update("smartlogistics.OrderMapper.resetAutoIncrement");
    }
    
    // ✅ 한 번에 여러 개의 주문을 DB에 삽입하는 메서드 추가
    public void batchInsertOrders(List<Order> orders) {
        sqlSession.insert("smartlogistics.OrderMapper.batchInsertOrders", orders);
    }

    // ✅ 주문 상태 업데이트
    public void updateOrderStatus(Long order_id, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", order_id);
        params.put("status", status);
        sqlSession.update("smartlogistics.OrderMapper.updateOrderStatus", params);
    }

    // ✅ 랜덤 주문 생성
    public void generateRandomOrders() {
        resetOrders();

        int orderCount = random.nextInt(1001) + 1000;
        Map<String, String> orderNumToDestination = new HashMap<>();
        Map<String, LocalDateTime> orderNumToTime = new HashMap<>();
        List<Order> orders = new ArrayList<>();

        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        long totalSeconds = ChronoUnit.SECONDS.between(startOfDay, startOfDay.plusDays(1));
        long interval = totalSeconds / orderCount;

        for (int i = 0; i < orderCount; i++) {
            Product randomProduct = getRandomProduct();
            if (randomProduct == null) continue;

            String order_num;
            String destination;
            LocalDateTime order_time;

            if (!orderNumToDestination.isEmpty() && random.nextDouble() < 0.1) {
                order_num = getRandomExistingOrderNum(orderNumToDestination);
                destination = orderNumToDestination.get(order_num);
                order_time = orderNumToTime.get(order_num);
            } else {
                order_num = generateRandomOrderNum();
                destination = getRandomCamp();
                order_time = startOfDay.plusSeconds(interval * i + random.nextInt((int) interval));
                orderNumToDestination.put(order_num, destination);
                orderNumToTime.put(order_num, order_time);
            }

            Order order = new Order();
            order.setOrder_num(order_num);
            order.setOrder_time(order_time);
            order.setDestination(destination);
            order.setBox_state(0);
            order.setProgress_state(0);
            order.setProduct_id(randomProduct.getProduct_id());
            order.setPallet_id(null);

            orders.add(order);
        }

        orders.sort(Comparator.comparing(Order::getOrder_time));
        batchInsertOrders(orders);
    }

    private String generateRandomOrderNum() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomDigits = String.format("%08d", random.nextInt(100000000));
        return datePrefix + randomDigits;
    }

    public List<Order> getOrdersByOrderNum(String order_num) {
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByOrderNum", order_num);
    }

    private String getRandomExistingOrderNum(Map<String, String> orderNumToDestination) {
        List<String> orderNums = new ArrayList<>(orderNumToDestination.keySet());
        return orderNums.get(random.nextInt(orderNums.size()));
    }

    public Product getRandomProduct() {
        return sqlSession.selectOne("smartlogistics.OrderMapper.getRandomProduct");
    }

    private String getRandomCamp() {
        List<String> camps = Arrays.asList("서초 캠프", "강남 캠프", "강서 캠프", "중구 캠프", "성동 캠프");
        return camps.get(random.nextInt(camps.size()));
    }
}
