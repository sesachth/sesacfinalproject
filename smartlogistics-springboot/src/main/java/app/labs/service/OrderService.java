package app.labs.service;

import app.labs.model.Order;
import app.labs.model.Product;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class OrderService {
    private final SqlSession sqlSession;
    private final Random random = new Random();

    public OrderService(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
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


    // ✅ 기존 데이터 삭제 + AUTO_INCREMENT 초기화
    public void resetOrders() {
        sqlSession.delete("smartlogistics.OrderMapper.deleteAllOrders");
        sqlSession.update("smartlogistics.OrderMapper.resetAutoIncrement");
    }
    
 // ✅ 한 번에 여러 개의 주문을 DB에 삽입하는 메서드 추가
    public void batchInsertOrders(List<Order> orders) {
        sqlSession.insert("smartlogistics.OrderMapper.batchInsertOrders", orders);
    }

 // ✅ 랜덤 주문 생성 (같은 orderNum을 가진 주문은 연속된 orderId 배정)
    public void generateRandomOrders() {
        resetOrders();  // ✅ 기존 주문 삭제 및 AUTO_INCREMENT 초기화

        int orderCount = random.nextInt(1001) + 1000; // ✅ 1000~2000개 주문 생성
        Map<String, LocalDateTime> orderNumToTime = new HashMap<>();
        Map<String, String> orderNumToDestination = new HashMap<>();
        Map<String, List<Order>> orderGroups = new LinkedHashMap<>(); // ✅ orderNum 별 그룹 저장 (순서 유지)

        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        long totalSeconds = ChronoUnit.SECONDS.between(startOfDay, startOfDay.plusDays(1)); // 하루 총 초
        long interval = totalSeconds / orderCount; // ✅ 주문 간의 시간 간격

        LocalDateTime lastOrderTime = startOfDay;  // ✅ 첫 주문 시간

        for (int i = 0; i < orderCount; i++) {
            Product randomProduct = getRandomProduct();
            if (randomProduct == null) continue;

            String camp = getRandomCamp();
            String orderNum;

            // ✅ 30% 확률로 기존 orderNum 사용 (그룹 주문 유지)
            if (!orderNumToTime.isEmpty() && random.nextDouble() < 0.3) {
                orderNum = getRandomExistingOrderNum(orderNumToTime);
                lastOrderTime = orderNumToTime.get(orderNum);  // ✅ 기존 orderNum이면 시간 유지
                camp = orderNumToDestination.get(orderNum);    // ✅ 기존 목적지 유지
            } else {
                orderNum = generateRandomOrderNum();
                lastOrderTime = startOfDay.plusSeconds(interval * i + random.nextInt((int) interval)); // ✅ 균등 분포 적용
                orderNumToTime.put(orderNum, lastOrderTime);
                orderNumToDestination.put(orderNum, camp);
            }

            Order order = new Order();
            order.setOrderNum(orderNum);
            order.setOrderTime(lastOrderTime);  // ✅ 시간 순서대로 생성
            order.setDestination(camp);
            order.setState("Pending");
            order.setProductId(randomProduct.getProductId());
            order.setPalletId(null);

            // ✅ orderNum 별로 그룹화하여 저장
            orderGroups.putIfAbsent(orderNum, new ArrayList<>());
            orderGroups.get(orderNum).add(order);
        }

        // ✅ 같은 orderNum을 가지는 주문들을 연속적으로 INSERT
        for (List<Order> groupOrders : orderGroups.values()) {
            batchInsertOrders(groupOrders);
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
        List<String> camps = Arrays.asList("서초 캠프", "강남 캠프", "강서 캠프", "중구 캠프", "성동 캠프");
        return camps.get(random.nextInt(camps.size()));
    }

    // ✅ 랜덤 8자리 숫자 생성
    private String generateRandomOrderNum() {
        return String.format("%08d", random.nextInt(100000000));
    }

    // ✅ 기존 orderNum 중 랜덤 선택
    private String getRandomExistingOrderNum(Map<String, LocalDateTime> orderNumToTime) {
        List<String> orderNums = new ArrayList<>(orderNumToTime.keySet());
        return orderNums.get(random.nextInt(orderNums.size()));
    }

    // ✅ 주문 저장
    public void insertOrder(Order order) {
        sqlSession.insert("smartlogistics.OrderMapper.insertOrder", order);
    }
}