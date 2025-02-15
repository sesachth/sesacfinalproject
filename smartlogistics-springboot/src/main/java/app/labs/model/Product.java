package app.labs.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Product {
    Long productId;
    String name;
    Float width;
    Float depth;
    Float height;
    Float weight;
    boolean fragile;
    String category;

    // 명확한 getter/setter 추가
    public boolean isFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }
}