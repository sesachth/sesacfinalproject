package app.labs.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Order {
    private Long orderId;
    private String orderNum;
    private LocalDateTime orderTime;
    private String destination;
    private String state;
    private Long productId;
    private Long palletId; // ✅ palletId 추가 (초기에는 NULL)

    public Order() {}

    public Order(Long orderId, String orderNum, LocalDateTime orderTime, String destination, String state, Long productId, Long palletId) {
        this.orderId = orderId;
        this.orderNum = orderNum;
        this.orderTime = orderTime;
        this.destination = destination;
        this.state = state;
        this.productId = productId;
        this.palletId = palletId;
    }
}
