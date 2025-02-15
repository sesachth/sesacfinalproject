package app.labs.service;

import app.labs.dao.ProductlistRepository;
import app.labs.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductlistRepository productlistRepository;

    @Autowired
    public ProductService(ProductlistRepository productlistRepository) {
        this.productlistRepository = productlistRepository;
    }

    public List<Product> getAllProducts() {
        return productlistRepository.findAll();
    }

    // 카테고리별 제품 조회 
    public List<Product> getProductByCategory(String category) {
        return productlistRepository.findByCategory(category);
    }
    
    // 취급주의별 제품 조회
    public List<Product> getProductByFragile(boolean fragile) {
        return productlistRepository.findByFragile(fragile);
    }

    // 카테고리와 취급 주의(Fragile) 값에 따른 제품 조회
    public List<Product> getProductByCategoryAndFragile(String category, boolean fragile) {
        return productlistRepository.findByCategoryAndFragile(category, fragile);
    }
    
    public void addProduct(Product product) {
        productlistRepository.insert(product);
    }

    public boolean updateProduct(Product product) {
        return productlistRepository.update(product) > 0;
    }

    public boolean deleteProductById(int productId) {
        return productlistRepository.delete(productId) > 0;
    }
    
    // 이름으로 제품 검색
    public List<Product> getProductsByName(String name) {
        return productlistRepository.findByNameContaining(name);
    }
    

}
