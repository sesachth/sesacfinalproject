package app.labs.dao;

import app.labs.model.ProgressDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import org.apache.ibatis.annotations.Update;


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
    
    // ✅ 진행 상태 업데이트 쿼리 추가
    @Update("UPDATE `order` SET progress_state = #{progressState} WHERE order_id = #{orderId}")
    void updateProgressState(@Param("orderId") Long orderId, @Param("progressState") int progressState);
}
