package app.labs.mapper;

import app.labs.model.Order;
import app.labs.model.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    // ✅ 기존 주문 데이터 삭제
    @Delete("DELETE FROM `order`")
    void clearOrders();

    // ✅ 현재 가장 큰 orderId 가져오기
    @Select("SELECT MAX(orderId) FROM `order`")
    Long getMaxOrderId();

    // ✅ 모든 상품 가져오기 (랜덤 선택을 위해 필요)
    @Select("SELECT * FROM product")
    List<Product> getAllProducts();

    // ✅ MySQL에서 직접 랜덤 상품 1개 가져오기
    @Select("SELECT * FROM product ORDER BY RAND() LIMIT 1")
    Product getRandomProduct();

    // ✅ 주문 저장 (orderId를 직접 설정, orderNum 추가)
    @Insert("INSERT INTO `order` (orderId, orderNum, orderTime, destination, state, product_productId) " +
            "VALUES (#{orderId}, #{orderNum}, #{orderTime}, #{destination}, #{state}, #{productProductId})")
    void insertOrder(Order order);

    // ✅ 특정 날짜의 주문 조회
    @Select("SELECT * FROM `order` WHERE DATE(orderTime) = #{date}")
    List<Order> getOrdersByDate(@Param("date") String date);

    // ✅ 전체 주문 조회
    @Select("SELECT * FROM `order`")
    List<Order> getAllOrders();

	List<Order> getOrdersByDestinationAndDate(String destination, String date);

	List<Order> getOrdersByDestination(String destination);
}
