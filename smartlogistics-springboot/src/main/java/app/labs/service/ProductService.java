package app.labs.service;

import app.labs.dao.ProductlistRepository;
import app.labs.model.Product;

import org.apache.ibatis.annotations.Param;
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

    // 移댄뀒怨좊━蹂� �쒗뭹 議고쉶 
    public List<Product> getProductByCategory(String category) {
        return productlistRepository.findByCategory(category);
    }
    
    // 痍④툒二쇱쓽蹂� �쒗뭹 議고쉶
    public List<Product> getProductByFragile(boolean isFragile) {
        return productlistRepository.findByFragile(isFragile);
    }

    // 移댄뀒怨좊━�� 痍④툒 二쇱쓽(Fragile) 媛믪뿉 �곕Ⅸ �쒗뭹 議고쉶
    public List<Product> getProductByCategoryAndFragile(String category, boolean isFragile) {
        return productlistRepository.findByCategoryAndFragile(category, isFragile);
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
    
    // �대쫫�쇰줈 �쒗뭹 寃���
    public List<Product> getProductsByName(String name) {
        return productlistRepository.findByNameContaining(name);
    }
    

}