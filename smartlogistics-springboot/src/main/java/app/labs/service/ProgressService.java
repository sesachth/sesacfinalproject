package app.labs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.labs.dao.ProgressDao;
import app.labs.model.ProgressDTO;

@Service
public class ProgressService {

    private final ProgressDao progressDao;
    private final SimpMessagingTemplate messagingTemplate;

    public ProgressService(ProgressDao progressDao, SimpMessagingTemplate messagingTemplate) {
        this.progressDao = progressDao;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ProgressDTO> getFilteredProgressList(
        int offset, int pageSize, String date, String camp, String orderNum, String boxSpec, Integer boxState, Integer progressState) {
        return progressDao.getFilteredProgressList(offset, pageSize, date, camp, orderNum, boxSpec, boxState, progressState);
    }

    public int getTotalFilteredRecords(
        String date, String camp, String orderNum, String boxSpec, Integer boxState, Integer progressState) {
        return progressDao.getTotalFilteredRecords(date, camp, orderNum, boxSpec, boxState, progressState);
    }

    @Transactional
    public void updateOrdersProgress(List<Integer> orderIds, int progressState, Integer imageNumber) {
        if (orderIds == null || orderIds.isEmpty()) {
            System.out.println("⚠️ [DB 업데이트] 주문 ID 없음, 업데이트 수행하지 않음");
            return;
        }

        // ✅ imageNumber가 없을 경우 null로 설정
        System.out.println("📌 [DB 업데이트] 진행 상태 업데이트 실행 - 주문 ID: " + orderIds + ", progressState: " + progressState + ", imageNumber: " + imageNumber);
        
        progressDao.updateOrdersProgress(orderIds, progressState, imageNumber);
        System.out.println("✅ [DB 업데이트] 완료 - 업데이트된 주문 ID: " + orderIds);
        
        sendImageUpdateToClients(orderIds, imageNumber);
    }
    
    public void sendImageUpdateToClients(List<Integer> orderIds, Integer imageNumber) {
        if (imageNumber == null) {
            System.out.println("⚠️ [이미지 전송] 이미지 번호 없음");
            return;
        }

        String imageUrl = "/images/boximages/" + String.format("%03d", imageNumber) + ".jpg";
        messagingTemplate.convertAndSend("/topic/updateImage", Map.of(
                "orderIds", orderIds,
                "imageUrl", imageUrl
        ));
        System.out.println("📡 [WebSocket 전송] 주문 ID: " + orderIds + ", URL: " + imageUrl);
    }
    
    public void updateBoxState(Long orderId, int boxState) {
    	progressDao.updateBoxState(orderId, boxState);  // MyBatis 매퍼 호출
    	
    	// WebSocket으로 상태 변경 알림
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("boxState", boxState);
        
        messagingTemplate.convertAndSend("/topic/updateImage", message);
        System.out.println("📦 박스 상태 업데이트 완료 - 주문 ID: " + orderId + ", 상태: " + boxState);
    }
}
