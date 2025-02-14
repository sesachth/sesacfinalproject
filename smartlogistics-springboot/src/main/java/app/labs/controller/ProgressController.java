package app.labs.controller;

import app.labs.service.ProgressService;
import app.labs.model.ProgressDTO;
import app.labs.model.ProgressMessage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.Payload;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    public String getProgressList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "camp", required = false) String camp,
            @RequestParam(value = "orderNum", required = false) String orderNum,
            Model model) {

        int pageSize = 20;
        int offset = (page - 1) * pageSize;

        List<ProgressDTO> progressList = progressService.getFilteredProgressList(offset, pageSize, date, camp, orderNum);
        int totalRecords = progressService.getTotalFilteredRecords(date, camp, orderNum);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        model.addAttribute("progressList", progressList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("page", "progress");

        return "thymeleaf/html/admin/admin_progress";
    }

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getProgressData(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "camp", required = false) String camp,
            @RequestParam(value = "orderNum", required = false) String orderNum) {

        int pageSize = 20;
        int offset = (page - 1) * pageSize;

        List<ProgressDTO> progressList = progressService.getFilteredProgressList(offset, pageSize, date, camp, orderNum);
        int totalRecords = progressService.getTotalFilteredRecords(date, camp, orderNum);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("progressList", progressList);
        response.put("totalPages", totalPages);
        return response;
    }
    
 // WebSocket을 통한 진행 상태 업데이트
    @MessageMapping("/updateProgress")
    @SendTo("/topic/progress")
    public String updateProgressStatus(@Payload ProgressMessage message) { // ✅ DTO 사용
        Long orderId = message.getOrderId();
        int progressState = message.getProgressState(); 

        progressService.updateProgressState(orderId, progressState);
        return "포장 완료";
    }
}
