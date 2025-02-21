package app.labs.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    private SqlSession sqlSession;

    public Map<String, List<Map<String, Object>>> getStackingResults() {
        Date currentDate = new Date(); // 현재 날짜
        List<Map<String, Object>> pallets = sqlSession.selectList("DataMapper.getStackingResults", currentDate);
        
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("pallets", pallets);
        
        return result;
    }
}
