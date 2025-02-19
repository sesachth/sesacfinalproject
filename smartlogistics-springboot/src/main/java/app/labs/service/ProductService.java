package app.labs.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.labs.dao.ProductlistRepository;
import app.labs.model.Product;

@Service
public class ProductService {

    private final ProductlistRepository productlistRepository;
    private final String FASTAPI_URL = "http://localhost:8000/api/v1/box_matching"; // FastAPI URL

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
        // FastAPI로 박스 매칭 결과 받기
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = null;
        
        try {
        	ObjectMapper mapper = new ObjectMapper();

            // Product 객체를 Map으로 변환
            Map<String, Object> productMap = mapper.convertValue(product, Map.class);
           
            
            // isFragile 필드 이름을 is_fragile로 변경
            if (productMap.containsKey("fragile")) {
                productMap.put("is_fragile", productMap.remove("fragile"));
                // Map을 JSON 형식으로 FastAPI에 전달
                response = restTemplate.postForObject(FASTAPI_URL, productMap, Map.class);
            } else {
                // Product를 JSON 형식으로 FastAPI에 전달
                response = restTemplate.postForObject(FASTAPI_URL, product, Map.class);   
            }
           
            if (response != null && response.get("spec") != null) {
                product.setSpec(Long.parseLong(response.get("spec").toString()));
            } else {
                product.setSpec(0L); // 박스 매칭 실패 시 default 값
            }

            productlistRepository.insert(product);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            // FastAPI 서버에서 에러 발생 시 처리
            System.err.println("Error during FastAPI communication: " + e.getMessage());
            product.setSpec(0L); // 기본값 처리
            productlistRepository.insert(product); // 계속해서 DB에 저장
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            product.setSpec(0L); // 기본값 처리
            productlistRepository.insert(product); // 계속해서 DB에 저장
        }

        // DB에 저장
        // productlistRepository.insert(product);
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