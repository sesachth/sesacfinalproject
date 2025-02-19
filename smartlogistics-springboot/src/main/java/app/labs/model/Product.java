package app.labs.model;

import lombok.Data;

@Data
public class Product {
	private Long productId;
	private String name;
	private float width;
	private float depth;
	private float height;
	private float weight;
	private boolean isFragile;
	private String category;
	private Long spec;
}