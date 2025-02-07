package app.labs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.labs.model.Product;
import app.labs.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
  
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(int productId) {
        return productRepository.findById(productId);
    }
    
    public void addProduct(Product product) {
        productRepository.insert(product);
    }
    
    public boolean updateProduct(Product product) {
        return productRepository.update(product) > 0;
    }
    
    public boolean deleteProductById(int productId) {
        return productRepository.delete(productId) > 0;
    }
}
