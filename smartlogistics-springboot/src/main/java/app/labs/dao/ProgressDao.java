package app.labs.dao;

import app.labs.model.ProgressDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
public interface ProgressDao {

    // 진행 상태 리스트 조회 (페이징 및 필터 적용)
    List<ProgressDTO> getFilteredProgressList(
        @Param("offset") int offset,
        @Param("pageSize") int pageSize,
        @Param("date") String date,
        @Param("camp") String camp,
        @Param("orderNum") String orderNum
    );

    // 필터링된 진행 상태 개수 조회
    int countTotalFilteredProgress(
        @Param("date") String date,
        @Param("camp") String camp,
        @Param("orderNum") String orderNum
    );
    
	 // ✅ 개별 주문 진행 상태 업데이트
	    @Update("UPDATE `order` SET progress_state = #{progressState} WHERE order_id = #{orderId}")
	    void updateOrderProgress(@Param("orderId") int orderId, @Param("progressState") int progressState);
	
	    // ✅ 모든 주문 진행 상태 업데이트
	    @Update("UPDATE `order` SET progress_state = #{progressState}")
	    void updateAllOrdersProgress(@Param("progressState") int progressState);
	
	    // ✅ 다중 주문 ID에 대해 진행 상태를 업데이트하는 메서드 추가
	    void updateOrdersProgress(@Param("orderIds") List<Integer> orderIds, @Param("progressState") int progressState);
}