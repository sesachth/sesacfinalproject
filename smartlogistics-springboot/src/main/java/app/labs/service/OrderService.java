package app.labs.service;

import app.labs.model.Order;
import app.labs.model.Product;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    private final SqlSession sqlSession;
    private final Random random = new Random();

    public OrderService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    // ✅ 전체 주문 조회
    public List<Order> getAllOrders() {
        return sqlSession.selectList("smartlogistics.OrderMapper.getAllOrders");
    }

    // ✅ 특정 목적지의 주문 조회
    public List<Order> getOrdersByDestination(String destination) {
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByDestination", destination);
    }

    // ✅ 특정 날짜의 주문 조회
    public List<Order> getOrdersByDate(String startOfDay, String endOfDay) {
        Map<String, Object> params = new HashMap<>();
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByDate", params);
    }

    // ✅ 특정 목적지 & 날짜 범위의 주문 조회
    public List<Order> getOrdersByDestinationAndDate(String destination, String startOfDay, String endOfDay) {
        Map<String, Object> params = new HashMap<>();
        params.put("destination", destination);
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByDestinationAndDate", params);
    }

    // ✅ 랜덤으로 1000~2000개의 주문 생성
    public void generateRandomOrders() {
        int orderCount = random.nextInt(1001) + 1000; // 1000~2000개
        Long currentMaxOrderId = getMaxOrderId();
        Map<String, String> orderNumToDestination = new HashMap<>();

        for (int i = 0; i < orderCount; i++) {
            // ✅ 랜덤 상품 가져오기
            Product randomProduct = getRandomProduct();
            if (randomProduct == null) continue; // 상품이 없으면 스킵

            // ✅ 랜덤 캠프 선택 (1~5)
            String camp = getRandomCamp();

            // ✅ 30% 확률로 기존 orderNum 사용 (그룹 주문 유지)
            String orderNum;
            if (!orderNumToDestination.isEmpty() && random.nextDouble() < 0.3) {
                orderNum = getRandomExistingOrderNum(orderNumToDestination);
                camp = orderNumToDestination.get(orderNum); // 기존 목적지 유지
            } else {
                orderNum = generateRandomOrderNum();
                orderNumToDestination.put(orderNum, camp); // 새로운 orderNum 저장
            }

            Order order = new Order();
            order.setOrderId(++currentMaxOrderId);
            order.setOrderNum(orderNum);
            order.setOrderTime(LocalDateTime.now());
            order.setDestination(camp);
            order.setState("Pending");
            order.setProductId(randomProduct.getProductId());
            order.setPalletId(null); // 초기에는 NULL

            insertOrder(order);
        }
    }

    // ✅ 현재 가장 큰 orderId 가져오기
    public Long getMaxOrderId() {
        Long maxOrderId = sqlSession.selectOne("smartlogistics.OrderMapper.getMaxOrderId");
        return (maxOrderId != null) ? maxOrderId : 0L;
    }

    // ✅ 랜덤 상품 가져오기
    public Product getRandomProduct() {
        return sqlSession.selectOne("smartlogistics.OrderMapper.getRandomProduct");
    }

    // ✅ 랜덤 캠프 가져오기
    private String getRandomCamp() {
        List<String> camps = Arrays.asList("서울 캠프", "강남 캠프", "강서 캠프", "중구 캠프", "성동 캠프");
        return camps.get(random.nextInt(camps.size()));
    }

    // ✅ 랜덤 8자리 숫자 생성
    private String generateRandomOrderNum() {
        return String.format("%08d", random.nextInt(100000000));
    }

    // ✅ 기존 orderNum 중 랜덤 선택
    private String getRandomExistingOrderNum(Map<String, String> orderNumToDestination) {
        List<String> orderNums = new ArrayList<>(orderNumToDestination.keySet());
        return orderNums.get(random.nextInt(orderNums.size()));
    }

    // ✅ 주문 저장
    public void insertOrder(Order order) {
        sqlSession.insert("smartlogistics.OrderMapper.insertOrder", order);
    }
}
