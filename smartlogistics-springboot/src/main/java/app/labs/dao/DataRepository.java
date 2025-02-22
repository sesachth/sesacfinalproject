package app.labs.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DataRepository {
    List<Map<String, Object>> getOrderCountByProgressState();
    List<Map<String, Object>> getBoxDamageRateByOrderTime();
    List<Map<String, Object>> getOrderCountByOrderTime();
    List<Map<String, Object>> getOrderCountByDestination();
    List<Map<String, Object>> getOrderCountBySpec();
    int getOrderCountByProgressState5(int progressState);
    List<Map<String, Object>> getStackingResults(Date currentDate);
}