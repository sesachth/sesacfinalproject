package app.labs.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.labs.dao.DataRepository;

@Service
public class DataService {

    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public List<Map<String, Object>> getOrderCountByProgressState() {
        return dataRepository.getOrderCountByProgressState();
    }

    public List<Map<String, Object>> getBoxDamageRateByOrderTime() {
        return dataRepository.getBoxDamageRateByOrderTime();
    }

    public List<Map<String, Object>> getOrderCountByOrderTime() {
        return dataRepository.getOrderCountByOrderTime();
    }

    public List<Map<String, Object>> getOrderCountByDestination() {
        List<Map<String, Object>> rawData = dataRepository.getOrderCountByDestination();
        return rawData.stream().map(this::processDestination).collect(Collectors.toList());
    }

    private Map<String, Object> processDestination(Map<String, Object> item) {
        String destination = (String) item.get("destination");
        String[] parts = destination.split("\\s+");
        if (parts.length > 0) {
            String firstWord = parts[0];
            if (firstWord.endsWith("구")) {
                item.put("destination", firstWord);
            } else {
                item.put("destination", firstWord + "구");
            }
        }
        return item;
    }

    public List<Map<String, Object>> getOrderCountBySpec() {
        return dataRepository.getOrderCountBySpec();
    }

    public int getOrderCountByProgressState5(int progressState) {
        return dataRepository.getOrderCountByProgressState5(progressState);
    }
    
    public Map<String, List<Map<String, Object>>> getStackingResults() {
        Date currentDate = new Date();
        List<Map<String, Object>> pallets = dataRepository.getStackingResults(currentDate);
        
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("pallets", pallets);
        
        return result;
    }
}