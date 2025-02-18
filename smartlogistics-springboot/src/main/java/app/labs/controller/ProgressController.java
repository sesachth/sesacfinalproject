package app.labs.controller;

import app.labs.service.ProgressService;
import app.labs.model.ProgressDTO;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        List<ProgressDTO> progressList = progressService.getFilteredProgressList(offset, pageSize, date, camp, orderNum, null, null, null);
        int totalRecords = progressService.getTotalFilteredRecords(date, camp, orderNum, null, null, null);
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
            @RequestParam(value = "orderNum", required = false) String orderNum,
            @RequestParam(value = "boxSpec", required = false) String boxSpec,
            @RequestParam(value = "boxState", required = false) Integer boxState,
            @RequestParam(value = "progressState", required = false) Integer progressState) {

        int pageSize = 20;
        int offset = (page - 1) * pageSize;

        List<ProgressDTO> progressList = progressService.getFilteredProgressList(offset, pageSize, date, camp, orderNum, boxSpec, boxState, progressState);
        int totalRecords = progressService.getTotalFilteredRecords(date, camp, orderNum, boxSpec, boxState, progressState);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("progressList", progressList);
        response.put("totalPages", totalPages);
        
        return response;
    }
    
    // ✅ WebSocket을 통해 "포장 완료" 메시지를 받으면 실행됨
    @MessageMapping("/updateStatus")
    @Transactional
    public void updateOrderStatus(@Payload Map<String, Object> payload) {
        System.out.println("📌 [WebSocket] 메시지 수신 - 데이터: " + payload);

        List<Integer> orderIds = (List<Integer>) payload.get("orderIds");
        int progressState = (int) payload.get("progressState");

        if (orderIds == null || orderIds.isEmpty()) {
            System.out.println("⚠️ [WebSocket] 주문 ID 없음, 업데이트 수행하지 않음");
            return;
        }

        // ✅ 선택된 주문만 업데이트하도록 변경
        progressService.updateOrdersProgress(orderIds, progressState);

        System.out.println("📌 [WebSocket] DB 업데이트 완료 - 업데이트된 주문 ID: " + orderIds);
    }
}
