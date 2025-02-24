package app.labs.controller;

import app.labs.service.ProgressService;
import app.labs.model.ProgressDTO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Getmapping, RequestMapping, RequestParam, ResponseBody 모두 포함.

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ProgressController(ProgressService progressService, SimpMessagingTemplate simpMessagingTemplate) {
        this.progressService = progressService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping
    public String getProgressList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "camp", required = false) String camp,
            @RequestParam(value = "orderNum", required = false) String orderNum,
            Model model) {

        int pageSize = 15;
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

        int pageSize = 15;
        int offset = (page - 1) * pageSize;

        List<ProgressDTO> progressList = progressService.getFilteredProgressList(offset, pageSize, date, camp, orderNum, boxSpec, boxState, progressState);
        int totalRecords = progressService.getTotalFilteredRecords(date, camp, orderNum, boxSpec, boxState, progressState);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("progressList", progressList);
        response.put("totalPages", totalPages);
        
        return response;
    }

    // ✅ WebSocket을 통해 "포장 완료" 메시지를 받으면 실행
    @MessageMapping("/updateStatus") 
    @Transactional
    public void updateOrderStatus(@Payload Map<String, Object> payload) {
        System.out.println("📌 [WebSocket] 메시지 수신 - 데이터: " + payload);

        List<Integer> orderIds = (List<Integer>) payload.get("orderIds");
        int progressState = (int) payload.get("progressState");


        String imageNumberStr = (String) payload.get("imageNumber");
        Integer imageNumber = imageNumberStr != null ? Integer.parseInt(imageNumberStr) : null;
        // ✅ imageNumber가 존재하면 사용, 없으면 null
        // Integer imageNumber = payload.containsKey("imageNumber") 
        //                         ? (Integer) payload.get("imageNumber") 
        //                         : null;

        if (orderIds == null || orderIds.isEmpty()) {
            System.out.println("⚠️ [WebSocket] 주문 ID 없음, 업데이트 수행하지 않음");
            return;
        }

        // ✅ DB 업데이트 (포장완료 등)
        progressService.updateOrdersProgress(orderIds, progressState, imageNumber);

        System.out.println("📌 [WebSocket] DB 업데이트 완료 - 업데이트된 주문 ID: " 
                           + orderIds + ", imageNumber: " + imageNumber);

        // ✅ 모든 클라이언트에게 상태 변경 내용 브로드캐스트
        Map<String, Object> broadcastMsg = new HashMap<>();
        broadcastMsg.put("orderIds", orderIds);
        broadcastMsg.put("progressState", progressState);
        broadcastMsg.put("imageNumber", imageNumber);

        // /topic/updateStatus 로 메시지 발행 → 구독 중인 클라이언트 모두에게 전송
        simpMessagingTemplate.convertAndSend("/topic/updateStatus", broadcastMsg);
    }

    /**
     * ✅ 엑셀 다운로드 기능 추가
     */
    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadProgressExcel(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "camp", required = false) String camp,
            @RequestParam(value = "orderNum", required = false) String orderNum,
            @RequestParam(value = "boxSpec", required = false) String boxSpec,
            @RequestParam(value = "boxState", required = false) Integer boxState,
            @RequestParam(value = "progressState", required = false) Integer progressState) {
        
        try {
            // ✅ 엑셀 데이터 생성
            byte[] excelFile = progressService.generateExcelFile(date, camp, orderNum, boxSpec, boxState, progressState);

            // ✅ 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=progress_data.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/check")
    public String checkBoxState(Model model) {
        model.addAttribute("page", "check");  // 현재 페이지 표시
        return "thymeleaf/html/admin/admin_check";  // admin_check.html로 이동
    }

    @GetMapping("/dataAll")
    @ResponseBody
    public Map<String, Object> getAllProgressData(
        @RequestParam(value = "date", required = false) String date,
        @RequestParam(value = "camp", required = false) String camp,
        @RequestParam(value = "orderNum", required = false) String orderNum,
        @RequestParam(value = "boxSpec", required = false) String boxSpec,
        @RequestParam(value = "boxState", required = false) Integer boxState,
        @RequestParam(value = "progressState", required = false) Integer progressState
    ) {
        // 여기서는 offset, pageSize 사용 X, 혹은 pageSize를 매우 큰 수로
        // 예: offset=0, pageSize=Integer.MAX_VALUE
        int offset = 0;
        int unlimitedSize = Integer.MAX_VALUE;

        // 페이징 없이 모든 항목 (필터는 동일하게)
        List<ProgressDTO> progressList = progressService.getFilteredProgressList(
                offset, unlimitedSize, date, camp, orderNum, boxSpec, boxState, progressState);

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("progressList", progressList);
        response.put("totalPages", 1); // 페이지네이션 없이 1로 처리
        return response;
    }
}


