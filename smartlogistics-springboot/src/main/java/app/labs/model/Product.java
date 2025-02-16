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
    float width;
    float depth;
    float height;
    float weight;
    boolean isFragile;
    String category;
    Long spec;;
}