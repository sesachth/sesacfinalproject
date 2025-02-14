package app.labs.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Order {
    private Long order_id;
    private String order_num;
    private LocalDateTime order_time;
    private String destination;
    private int box_state; // 0: 미검사, 1: 박스정상, 2: 박스파손
    private int progress_state; // 0: 물품대기, 1: 포장완료, 2: 적재완료
    private Long product_id;
    private Long pallet_id; // ✅ pallet_id 추가 (초기에는 NULL)

    // ✅ JSON 변환용 필드 추가
    private String formattedOrderTime;

    public Order() {}

    public Order(Long order_id, String order_num, LocalDateTime order_time, String destination, String state, Long product_id, Long pallet_id) {
        this.order_id = order_id;
        this.order_num = order_num;
        this.order_time = order_time;
        this.destination = destination;
        this.product_id = product_id;
        this.pallet_id = pallet_id;
    }

    public String getFormattedOrderTime() {
        return formattedOrderTime;
    }

    public void setFormattedOrderTime(String formattedOrderTime) {
        this.formattedOrderTime = formattedOrderTime;
    }
}
