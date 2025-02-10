package app.labs.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Order {
    private Long orderId;
    private String orderNum; // ✅ 주문 그룹 번호 추가
    private LocalDateTime orderTime;
    private String destination;
    private String state; // ✅ 주문 상태 추가
    private Long productProductId;

    // 기본 생성자
    public Order() {}

    // 전체 필드를 포함하는 생성자 *
    public Order(Long orderId, String orderNum, LocalDateTime orderTime, String destination, String state, Long productProductId) {
        this.orderId = orderId;
        this.orderNum = orderNum;
        this.orderTime = orderTime;
        this.destination = destination;
        this.state = state;
        this.productProductId = productProductId;
    }
}

