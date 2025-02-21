package app.labs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDTO {
    private int orderId;
    private String orderNum;
    private Timestamp orderTime;
    private int boxSpec;  // 🔄 ✅ 기존: String → 변경: int
    private String destination;
    private Integer palletId;
    private int boxState;
    private int progressState;
    private String productName;
    private String productCategory;
    private Boolean isFragile;

    // Getter & Setter
    public int getBoxSpec() {
        return boxSpec;
    }

    public void setBoxSpec(int boxSpec) {
        this.boxSpec = boxSpec;
    }
    
    // ✅ Getter에서 변환 로직 추가
    public boolean getIsFragile() {
        return isFragile != null && isFragile;
    }
    
    public void setIsFragile(Boolean isFragile) {
        this.isFragile = isFragile;
    }
}
