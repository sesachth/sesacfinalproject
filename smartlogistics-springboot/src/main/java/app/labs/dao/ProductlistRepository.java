package app.labs.dao;

import org.apache.ibatis.annotations.Mapper;
import app.labs.model.Product;
import java.util.List;

@Mapper
public interface ProductlistRepository {

    // ✅ 전체 물품 목록 조회
    List<Product> findAll();

    // ✅ 특정 물품 단건 조회 (productId 기준)
    Product findById(int productId);

    // ✅ 신규 물품 추가
    void insert(Product product);

    // ✅ 기존 물품 수정
    int update(Product product);

    // ✅ 특정 물품 삭제
    int delete(int productId);
}
