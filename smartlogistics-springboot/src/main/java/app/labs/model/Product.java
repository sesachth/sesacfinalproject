package app.labs.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Product {
    Long product_id;  // ✅ 컬럼명 수정 (productId → product_id)
    String name;
    float width;
    float depth;
    float height;
    float weight;
    boolean is_fragile;  // ✅ 컬럼명 수정 (fragile → is_fragile)
    String category;

    // ✅ 명확한 getter/setter 추가
    public boolean isIs_fragile() {
        return is_fragile;
    }

    public void setIs_fragile(boolean is_fragile) {
        this.is_fragile = is_fragile;
    }
}
