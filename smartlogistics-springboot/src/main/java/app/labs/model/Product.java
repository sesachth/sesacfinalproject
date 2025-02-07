package app.labs.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Product {
    private Long productId;
    private String name;
    private String category;

    int productId;
    String name;
    float width;
    float depth;
    float height;
    float weight;
    boolean fragile;
    String category;

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

    // 명확한 getter/setter 추가
    public boolean isFragile() {
        return fragile;
    }

    // 기본 생성자
    public Product() {}

    // 전체 필드를 포함하는 생성자
    public Product(Long productId, String name, String category) {
        this.productId = productId;
        this.name = name;
        this.category = category;
    }

    // Getter 및 Setter
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
