package app.labs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.labs.dao.ProductlistRepository;
import app.labs.model.Product;


@Service
public class ProductService {

	
    //private final ProductRepository productRepository;
    
    private final ProductlistRepository productlistRepository;
  
    @Autowired
    public ProductService(ProductlistRepository productlistRepository) {
        this.productlistRepository = productlistRepository;
    }
    
    public List<Product> getAllProducts() {
        return productlistRepository.findAll();
    }
    
    public Product getProductById(int productId) {
        return productlistRepository.findById(productId);
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
    
}
