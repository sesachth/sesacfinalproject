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
    
    @GetMapping("/stackingResults")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getStackingResults() {
        Map<String, List<Map<String, Object>>> stackingResults = dataService.getStackingResults();
        return ResponseEntity.ok(stackingResults);
    }
}
