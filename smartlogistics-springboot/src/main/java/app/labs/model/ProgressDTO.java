package app.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDTO {
    private int orderId;            // 주문 ID
    private String orderNum;        // 주문 번호
    private Timestamp orderTime;    // 주문 시간
    private String boxSpec;
    private String destination;     // 배송 목적지
    private Integer palletId;       // 팔렛트 ID (NULL 가능)
    private int boxState;           // 박스 상태 (0: 미검사, 1: 정상, 2: 파손)
    private int progressState;      // 진행 상태 (0: 물품 대기, 1: 포장 완료, 2: 적재 완료)
    private String productName;     // 상품명 (`p.name AS productName`에 매핑됨)
    private String productCategory; // 상품 카테고리 (`p.category`에 매핑됨)
    
 // Getter & Setter 추가
    public String getBoxSpec() {
        return boxSpec;
    }

    public void setBoxSpec(String boxSpec) {
        this.boxSpec = boxSpec;
    }
}
