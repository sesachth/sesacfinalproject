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
    private int boxState; // 0: 미검사, 1: 박스정상, 2: 박스파손
    private int progressState; // 0: 물품대기, 1: 포장완료, 2: 적재완료
    private Long productId;
    private Long palletId; // ✅ palletId 추가 (초기에는 NULL)

 // ✅ JSON 변환용 필드 추가
    private String formattedOrderTime;

    // ✅ Getter/Setter 추가
    public String getFormattedOrderTime() {
        return formattedOrderTime;
    }

    public void setFormattedOrderTime(String formattedOrderTime) {
        this.formattedOrderTime = formattedOrderTime;
    }
    
    public Order() {}

    public Order(Long orderId, String orderNum, LocalDateTime orderTime, String destination, String state, Long productId, Long palletId) {
        this.orderId = orderId;
        this.orderNum = orderNum;
        this.orderTime = orderTime;
        this.destination = destination;
        this.productId = productId;
        this.palletId = palletId;
    }
}
