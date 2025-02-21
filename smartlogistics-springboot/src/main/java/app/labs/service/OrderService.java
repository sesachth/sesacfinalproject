package app.labs.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.ibatis.session.SqlSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import app.labs.model.Order;
import app.labs.model.Product;

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

	// ✅ 일주일치 랜덤 주문 생성 (같은 orderNum을 가진 주문은 연속된 orderId 배정)
    public void generateRandomOrders() {
        resetOrders();  // ✅ 기존 주문 삭제 및 AUTO_INCREMENT 초기화
        List<Order> orders = new ArrayList<>();

        LocalDate startDate = LocalDate.now().minusDays(6);  // ✅ 최근 7일 (오늘 포함)
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            generateDailyOrders(orders, currentDate);  // ✅ 하루 단위 주문 생성 (하지만 삽입은 여기서)
        }

        // ✅ 주문 시간을 기준으로 정렬하여 순서 보장
        orders.sort(Comparator.comparing(Order::getOrderTime));

        System.out.println("📌 최종 삽입할 주문 개수: " + orders.size());
        batchInsertOrders(orders);  // ✅ 최종 한 번만 DB에 삽입
        readStackingResultsFromFastAPI();
    }

 // ✅ 하루 1000~2000개 랜덤 주문 생성 (오늘 날짜 주문의 palletId는 NULL)
    private void generateDailyOrders(List<Order> orders, LocalDate date) {
        int orderCount = random.nextInt(1001) + 1000; // ✅ 1000~2000개 주문 생성
        Map<String, String> orderNumToDestination = new HashMap<>();  // ✅ 주문번호 → 목적지
        Map<String, LocalDateTime> orderNumToTime = new HashMap<>();  // ✅ 주문번호 → 주문 시간

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        long totalSeconds = ChronoUnit.SECONDS.between(startOfDay, startOfDay.plusDays(1));
        long interval = totalSeconds / orderCount;

        for (int i = 0; i < orderCount; i++) {
            Product randomProduct = getRandomProduct();
            if (randomProduct == null) continue;

            String orderNum;
            String destination;
            LocalDateTime orderTime;

            if (!orderNumToDestination.isEmpty() && random.nextDouble() < 0.1) {  // ✅ 기존 주문번호 재사용 (10% 확률)
                orderNum = getRandomExistingOrderNum(orderNumToDestination);
                destination = orderNumToDestination.get(orderNum);
                orderTime = orderNumToTime.get(orderNum);
            } else {
                orderNum = generateRandomOrderNum(date);
                destination = getRandomCamp();
                orderTime = startOfDay.plusSeconds(interval * i + random.nextInt((int) interval));
                orderNumToDestination.put(orderNum, destination);
                orderNumToTime.put(orderNum, orderTime);
            }

            Order order = new Order();
            order.setOrderNum(orderNum);
            order.setOrderTime(orderTime);
            order.setDestination(destination);
            order.setProductId(randomProduct.getProductId());

            if (date.equals(today)) {
                order.setBoxState(0);  // ✅ 오늘 날짜 주문은 미검사(0)
                order.setProgressState(0);  // ✅ 오늘 날짜 주문은 물품대기(0)
                order.setPalletId(null);  // ✅ 오늘 날짜 주문은 palletId NULL 처리
                System.out.println("✅ 오늘 날짜 주문 생성됨: " + orderNum + " | boxState: " + order.getBoxState() + " | progressState: " + order.getProgressState() + " | palletId: NULL");
            } else {
                order.setBoxState(random.nextDouble() < 0.7 ? 1 : 2);  // ✅ 70% 정상(1), 30% 손상(2)
                order.setProgressState(2);  // ✅ 적재 완료 상태
                order.setPalletId((long) (random.nextInt(150) + 1));  // ✅ 과거 주문은 랜덤 palletId 부여
            }

            orders.add(order);
        }

        System.out.println("📌 " + date + " 주문 개수: " + orders.size());
    }


    // ✅ 주문번호를 YYMMDD + 8자리 랜덤 숫자로 생성
    private String generateRandomOrderNum(LocalDate date) {
        String datePrefix = date.format(DateTimeFormatter.ofPattern("yyMMdd"));
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
    
 // ✅ 오늘 날짜의 진행 중인 주문 조회
    public List<Order> getOrdersInProgress() {
        return sqlSession.selectList("smartlogistics.OrderMapper.getOrdersInProgress");
    }
    
    public void readStackingResultsFromFastAPI() {
    	String fastApiUrl = "http://localhost:8000/api/v1/stacking_results";
    	RestTemplate restTemplate = new RestTemplate();
    	
    	try {
            ResponseEntity<String> response = restTemplate.getForEntity(fastApiUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String body = response.getBody();
                if (body != null && body.contains("\"message\": \"OK\"")) {
                    // FastAPI 응답이 정상입니다.
                    return;
                }
            } 
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        	System.err.println("FastAPI 서버 오류: " + e.getMessage());
        } catch (Exception e) {
        	System.err.println("FastAPI 연결 실패: " + e.getMessage());
        }
    }

}
