package app.labs.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.labs.service.DataService;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private DataService dataService;
    
    @GetMapping("/dashboard/chart1Data")
    public List<Map<String, Object>> getChart1Data() {
        return dataService.getOrderCountByProgressState();
    }
    
    @GetMapping("/dashboard/chart2Data")
    public List<Map<String, Object>> getChart2Data() {
        return dataService.getBoxDamageRateByOrderTime();
    }
    
    @GetMapping("/dashboard/chart3Data")
    public List<Map<String, Object>> getChart3Data() {
        return dataService.getOrderCountByOrderTime();
    }
    
    @GetMapping("/dashboard/chart4Data")
    public List<Map<String, Object>> getChart4Data() {
        return dataService.getOrderCountByDestination();
    }
    
    @GetMapping("/dashboard/chart5Data")
    public List<Map<String, Object>> getChart5Data() {
        return dataService.getOrderCountBySpec();
    }
    
    @GetMapping("/stacking/stackingResults")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getStackingResults() {
        Map<String, List<Map<String, Object>>> stackingResults = dataService.getStackingResults();
        return ResponseEntity.ok(stackingResults);
    }
}
