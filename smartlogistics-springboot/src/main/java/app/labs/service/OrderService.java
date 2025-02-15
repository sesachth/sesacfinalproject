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
    public Order getOrderById(Long orderId) {
        return sqlSession.selectOne("smartlogistics.OrderMapper.getOrderById", orderId);
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

    // ✅ 엑셀 다운로드용 전체 주문 조회 (페이징 X)
    public List<Order> getAllOrdersForExport() {
        return sqlSession.selectList("smartlogistics.OrderMapper.getAllOrdersForExport");
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
    public void deleteOrder(Long orderId) {
        sqlSession.delete("smartlogistics.OrderMapper.deleteOrder", orderId);
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
    public void updateOrderStatus(Long orderId, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("status", status);
        sqlSession.update("smartlogistics.OrderMapper.updateOrderStatus", params);
    }

 // ✅ 랜덤 주문 생성 (같은 orderNum을 가진 주문은 연속된 orderId 배정)
    public void generateRandomOrders() {
        resetOrders();  // ✅ 기존 주문 삭제 및 AUTO_INCREMENT 초기화

        int orderCount = random.nextInt(1001) + 1000; // ✅ 1000~2000개 주문 생성
        Map<String, String> orderNumToDestination = new HashMap<>();  // ✅ 주문번호 → 목적지
        Map<String, LocalDateTime> orderNumToTime = new HashMap<>();  // ✅ 주문번호 → 주문 시간
        List<Order> orders = new ArrayList<>();

        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        long totalSeconds = ChronoUnit.SECONDS.between(startOfDay, startOfDay.plusDays(1));
        long interval = totalSeconds / orderCount;

        for (int i = 0; i < orderCount; i++) {
            Product randomProduct = getRandomProduct();
            if (randomProduct == null) continue;

            String orderNum;
            String destination;
            LocalDateTime orderTime;

            if (!orderNumToDestination.isEmpty() && random.nextDouble() < 0.1) {  // ✅ 기존 주문번호 재사용 (10% 확률)
                orderNum = getRandomExistingOrderNum(orderNumToDestination);  // ✅ 기존 주문번호 선택
                destination = orderNumToDestination.get(orderNum);  // ✅ 기존 목적지 유지
                orderTime = orderNumToTime.get(orderNum);  // ✅ 기존 주문 시간 유지
            } else {
                orderNum = generateRandomOrderNum();
                destination = getRandomCamp();  // ✅ 새로운 주문번호는 새로운 목적지 지정
                orderTime = startOfDay.plusSeconds(interval * i + random.nextInt((int) interval));
                orderNumToDestination.put(orderNum, destination);  // ✅ 주문번호 → 목적지 저장
                orderNumToTime.put(orderNum, orderTime);  // ✅ 주문번호 → 주문시간 저장
            }

            Order order = new Order();
            order.setOrderNum(orderNum);
            order.setOrderTime(orderTime);
            order.setDestination(destination);  // ✅ 동일한 orderNum일 경우 같은 목적지 유지
            order.setBoxState(0);  // ✅ 기본값: 미검사
            order.setProgressState(0);  // ✅ 기본값: 물품대기
            order.setProductId(randomProduct.getProductId());
            order.setPalletId(null);

            orders.add(order);
        }

        orders.sort(Comparator.comparing(Order::getOrderTime));  // ✅ 주문 시간을 기준으로 정렬
        batchInsertOrders(orders);  // ✅ 일괄 삽입
    }


    // ✅ 주문번호를 YYMMDD + 8자리 랜덤 숫자로 생성
    private String generateRandomOrderNum() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomDigits = String.format("%08d", random.nextInt(100000000));
        return datePrefix + randomDigits;
    }
    
 // ✅ 주문번호로 주문 조회
    public List<Order> getOrdersByOrderNum(String orderNum) {
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersByOrderNum", orderNum);
    }


    // ✅ 기존 orderNum 중 랜덤 선택
    private String getRandomExistingOrderNum(Map<String, String> orderNumToDestination) {
        List<String> orderNums = new ArrayList<>(orderNumToDestination.keySet());
        return orderNums.get(random.nextInt(orderNums.size()));
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
}
