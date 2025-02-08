package app.labs.dao;

import app.labs.model.Product;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductlistRepository {

    // 전체 물품 목록 조회
    List<Product> findAll();

    // 특정 물품 단건 조회 (productId 기준)
    Product findById(int productId);

    // 신규 물품 추가 (삽입 성공 시 영향받은 행의 수 반환)
    int insert(Product product);

    // 기존 물품 수정 (수정 성공 시 영향받은 행의 수 반환)
    int update(Product product);

    // 특정 물품 삭제 (삭제 성공 시 영향받은 행의 수 반환)
    int delete(int productId);
}


