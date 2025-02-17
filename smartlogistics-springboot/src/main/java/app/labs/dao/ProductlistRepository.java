package app.labs.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import app.labs.model.Product;
import java.util.List;

@Mapper
public interface ProductlistRepository {
    // �꾩껜 臾쇳뭹 紐⑸줉 議고쉶
    List<Product> findAll();
    
    // �뱀젙 臾쇳뭹 移댄뀒怨좊━ 議고쉶 (�뚮씪誘명꽣濡� category瑜� 諛쏆쓬)
    List<Product> findByCategory(@Param("category")String category);
 
    // �뱀젙 臾쇳뭹 痍④툒二쇱쓽 議고쉶 
    List<Product> findByFragile(@Param("isFragile")boolean isFragile);
 
    // �뱀젙 臾쇳뭹 痍④툒二쇱쓽&移댄뀒怨좊━ 議고쉶
    List<Product> findByCategoryAndFragile(@Param("category")String category, @Param("isFragile")boolean isFragile);
    
    // �좉퇋 臾쇳뭹 異붽�
    void insert(Product product);
    
    // 湲곗〈 臾쇳뭹 �섏젙
    int update(Product product);
    
    // �뱀젙 臾쇳뭹 ��젣
    int delete(int productId);
    
 // �뱀젙 �대쫫�쇰줈 �쒗뭹 寃���
    List<Product> findByNameContaining(String name);
	
}