package app.labs.dao;

import org.apache.ibatis.annotations.Mapper;
import app.labs.model.Product;
import java.util.List;

@Mapper
public interface ProductlistRepository {
    // 전체 물품 목록 조회
    List<Product> findAll();
    
    // 특정 물품 카테고리 조회 (파라미터로 category를 받음)
    List<Product> findByCategory(String category);
 
    // 특정 물품 취급주의 조회 
    List<Product> findByFragile(boolean fragile);
 
    // 특정 물품 취급주의&카테고리 조회
    List<Product> findByCategoryAndFragile(String category, boolean fragile);
    
    // 신규 물품 추가
    void insert(Product product);
    
    // 기존 물품 수정
    int update(Product product);
    
    // 특정 물품 삭제
    int delete(int productId);
    
 // 특정 이름으로 제품 검색
    List<Product> findByNameContaining(String name);
    
	
}
