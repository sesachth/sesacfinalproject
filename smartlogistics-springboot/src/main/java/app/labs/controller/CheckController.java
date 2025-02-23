package app.labs.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import app.labs.service.ProgressService;

@Controller
public class CheckController {
	
	/*
	private final ProgressService progressService;
	private final SimpMessagingTemplate messagingTemplate;
	
	public CheckController(ProgressService progressService, SimpMessagingTemplate messagingTemplate) {
        this.progressService = progressService;
        this.messagingTemplate = messagingTemplate;
    }
    */
	
	@GetMapping("/admin/check")
    public String mainPage(Model model) {
        return "thymeleaf/html/admin/admin_check";
    }
	
	/*
	@PostMapping("/api/v1/update-box-state")
    public ResponseEntity<?> updateBoxState(@RequestBody Map<String, Object> payload) {
        try {
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            int boxState = (int) Double.parseDouble(payload.get("boxState").toString());
            
            // DB 업데이트
            progressService.updateBoxState(orderId, boxState);
            
            // WebSocket으로 상태 변경 알림
            // messagingTemplate.convertAndSend("/topic/updateImage", payload);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    */
}
