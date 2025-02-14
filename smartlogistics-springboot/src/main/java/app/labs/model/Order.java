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
    private int boxState;
    private int progressState;
    private Long productId;
    private Long palletId;

    private String formattedOrderTime;

    public Order() {}

    public Order(Long orderId, String orderNum, LocalDateTime orderTime, String destination, int boxState, int progressState, Long productId, Long palletId) {
        this.orderId = orderId;
        this.orderNum = orderNum;
        this.orderTime = orderTime;
        this.destination = destination;
        this.boxState = boxState;
        this.progressState = progressState;
        this.productId = productId;
        this.palletId = palletId;
    }

    public String getFormattedOrderTime() {
        return formattedOrderTime;
    }

    public void setFormattedOrderTime(String formattedOrderTime) {
        this.formattedOrderTime = formattedOrderTime;
    }

    // ✅ 명시적인 Getter 추가 (Camel Case 적용)
    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public Long getProductId() {
        return productId;
    }
}
